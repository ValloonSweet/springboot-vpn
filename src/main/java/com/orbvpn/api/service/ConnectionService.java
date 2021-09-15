package com.orbvpn.api.service;

import com.jcraft.jsch.JSchException;
import com.orbvpn.api.domain.dto.ConnectionHistoryView;
import com.orbvpn.api.domain.dto.OnlineSessionView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.RadAcct;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.ConnectionMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.ConnectionRepository;
import com.orbvpn.api.reposiitory.ServerRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.service.common.AesUtil;
import com.orbvpn.api.service.common.SshUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConnectionService {

    private static final boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final ConnectionMapper connectionMapper;
    private final UserViewMapper userViewMapper;
    @Value("${application.secure-key}")
    private String secureKey;

    public static String encryptServerPass(String serverPass, String secretKey) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        IvParameterSpec ivParameterSpec = AesUtil.generateIv();
        String ivStr = AesUtil.convertIvParameterSpecToString(ivParameterSpec);
        String cypherText = AesUtil.encrypt(serverPass, secretKey, ivStr);
        return ivStr + cypherText;
    }

    public static String decryptServerPass(String cypherText, String secretKey) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String ivStr = cypherText.substring(0, 24);
        String encryptedPass = cypherText.substring(24);
        return AesUtil.decrypt(encryptedPass, secretKey, ivStr);
    }

    public List<ConnectionHistoryView> getConnectionHistory(Integer userId) {
        List<RadAcct> radaccts = connectionRepository.findConnectionHistory(userId);
        return radaccts.stream()
                .map(connectionMapper::connectionHistoryView)
                .collect(Collectors.toList());
    }

    public List<OnlineSessionView> getOnlineSessions(Integer userId) {
        List<RadAcct> radaccts = connectionRepository.findOnlineSessions(userId);
        return radaccts.stream()
                .map(connectionMapper::onlineSessionView)
                .collect(Collectors.toList());
    }

    public Page<UserView> getOnlineUsers(Integer page, Integer size, Integer serverId, Integer groupId,
                                         Integer roleId, Integer serviceGroupId) {
        Pageable pageable = PageRequest.of(page, size);//, Sort.by(DEFAULT_SORT_NATIVE)
        Page<User> users = userRepository.findOnlineUsers(pageable, serverId, groupId, roleId, serviceGroupId);
        return users.map(userViewMapper::toView);
    }

    public Boolean disconnect(String onlineSessionId) {
        RadAcct radAcct = connectionRepository.findByAcctsessionid(onlineSessionId);
        if (radAcct == null)
            throw new RuntimeException("Invalid sessionId");
        else if (!radAcct.isOnlineSession())
            throw new RuntimeException("This session is not online");
        String userNameToKill = radAcct.getUsername();
        String serverPublicIpAddress = radAcct.getNasipaddress();
        Server server = serverRepository.findByPrivateIp(serverPublicIpAddress);

        if (server == null)
            throw new RuntimeException("Undefined server with ip = " + serverPublicIpAddress);

        Map<String, String> values = new HashMap<>();
        values.put("username", userNameToKill);
        String killCommand = StringSubstitutor.replace(server.getKillCommand(), values, "{", "}");

        String response = null;
        if (server.getSshKey() != null && !server.getSshKey().equals("")) {
            String password = null;
            try {
                password = decryptServerPass(server.getSshKey(), secureKey);
            } catch (Exception e) {
                log.error("error in decrypting server password at disconnect request.");
                throw new RuntimeException("error in decrypting server password ");
            }
            try {
                if (server.getRootCommand() != null && !server.getRootCommand().equals("")) {
                    response = SshUtil.executeCommandUsingPss(server.getSshUsername(), server.getRootCommand(),
                            server.getPublicIp(), server.getPorts(), server.getRootCommand());
                    log.debug("ssh root command response: " + response);
                }
                response = SshUtil.executeCommandUsingPss(server.getSshUsername(), password,
                        server.getPublicIp(), server.getPorts(), killCommand);
                log.debug("ssh kill command response: " + response);
            } catch (Exception e) {
                log.error("failed to execute disconnect commands.", e);
                throw new RuntimeException("failed to execute disconnect commands.");
            }
        } else if (server.getSshPrivateKey() != null && !server.getSshPrivateKey().equals("")) {
            Path privateKeyFile = null;
            try {
                String filePrefix = "sshPrivateKey-" + userNameToKill + "-" + System.currentTimeMillis();
                if (isPosix) {
                    Set<PosixFilePermission> fp =
                            PosixFilePermissions.fromString("rwxrwxrwx");// 777
                    privateKeyFile = Files.createTempFile(filePrefix, ".pem",
                            PosixFilePermissions.asFileAttribute(fp));
                } else {
                    privateKeyFile = Files.createTempFile(filePrefix, ".pem");
                }
            } catch (IOException e) {
                log.error("failed to create security file", e);
            }
            try {
                log.debug("Temp file : " + privateKeyFile + " is created.");
                Files.write(privateKeyFile, server.getSshPrivateKey().getBytes());

                String privateKeyAbsolutePath = privateKeyFile.toFile().getAbsolutePath();


                if (server.getRootCommand() != null && !server.getRootCommand().equals("")) {
                    response = SshUtil.executeCommandUsingPrivateKey(server.getSshUsername(), privateKeyAbsolutePath,
                            server.getPublicIp(), server.getPorts(), server.getRootCommand());
                    log.debug("ssh root command response: " + response);
                }
                response = SshUtil.executeCommandUsingPrivateKey(server.getSshUsername(), privateKeyAbsolutePath,
                        server.getPublicIp(), server.getPorts(), killCommand);
                log.debug("ssh kil command response: " + response);
            } catch (IOException e) {
                log.error("Fail to prepare security key file for connecting to server", e);
                throw new RuntimeException("Fail to prepare security key file for connecting to server", e);
            } catch (JSchException e) {
                log.error("Fail to execute ssh command", e);
                throw new RuntimeException("Fail to execute ssh command", e);
            } catch (InterruptedException e) {
                log.error("interruption error", e);
                throw new RuntimeException("interruption error", e);
            } finally {
                try {
                    Files.delete(privateKeyFile);
                } catch (IOException e) {
                    log.error("failed to delete temp file", e);
                }
            }
        } else {
            throw new RuntimeException("Both ssh password and private key are not specified for server with id = " + server.getId());
        }
        log.info("disconnect request is done for session id:" + onlineSessionId);
        return true;
    }
}
