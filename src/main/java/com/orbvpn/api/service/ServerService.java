package com.orbvpn.api.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.orbvpn.api.domain.dto.ClientServerView;
import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.domain.entity.CongestionLevel;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ServerEditMapper;
import com.orbvpn.api.mapper.ServerViewMapper;
import com.orbvpn.api.reposiitory.CongestionLevelRepository;
import com.orbvpn.api.reposiitory.ServerRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import com.orbvpn.api.service.common.SshUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServerService {
  private final ServerRepository serverRepository;
  private final CongestionLevelRepository congestionLevelRepository;
  private final ServerEditMapper serverEditMapper;
  private final ServerViewMapper serverViewMapper;
  private final UserService userService;

  private final RadiusService radiusService;

  public ServerView createServer(ServerEdit serverEdit) {
    log.info("Creating server with data {}", serverEdit);
    Server server = serverEditMapper.create(serverEdit);

    serverRepository.save(server);
    radiusService.createNas(server);
    ServerView serverView = serverViewMapper.toView(server);
    log.info("Created server {}", serverView);
    return serverView;
  }

  public ServerView editServer(int id, ServerEdit serverEdit) {
    log.info("Editing server with id {} with data {}", id, serverEdit);

    Server server = getServerById(id);
    String publicIp = server.getPublicIp();
    server = serverEditMapper.edit(server, serverEdit);
    serverRepository.save(server);
    radiusService.editNas(publicIp, server);

    ServerView serverView = serverViewMapper.toView(server);
    log.info("Edited server {}", serverView);
    return serverView;
  }

  public ServerView deleteServer(int id) {
    log.info("Deleting server with id {}", id);

    Server server = getServerById(id);
    serverRepository.delete(server);
    radiusService.deleteNas(server);

    ServerView serverView = serverViewMapper.toView(server);
    log.info("Deleted server {}", serverView);
    return serverView;
  }

  public ServerView getServer(int id) {
    Server server = getServerById(id);

    return serverViewMapper.toView(server);
  }

  public List<ServerView> getServers() {
    return serverRepository.findAll()
            .stream()
            .map(serverViewMapper::toView)
            .collect(Collectors.toList());
  }

  public List<ClientServerView> getClientServers() {
    return serverRepository.findAll()
            .stream()
            .map(serverViewMapper::toClientView)
            .collect(Collectors.toList());
  }

  public List<ClientServerView> getClientSortedServers(String sortBy, String parameter) {
    List<ClientServerView> ClientServerViewList = getClientServers();
    User user = userService.getUser();

    switch (sortBy) {
      case "recent-connection":
        String email = user.getEmail();
        List<ClientServerView> recentConnectedServers = serverRepository.findServerByRecentConnection(email).stream().map(serverViewMapper::toClientView).collect(Collectors.toList());
        return recentConnectedServers;
      case "congestion":
        List<Server> ServerList = serverRepository.findAll()
                .stream()
                .collect(Collectors.toList());

        List<CongestionLevel> CongestionLevelList = congestionLevelRepository.findAll()
                .stream()
                .collect(Collectors.toList());

        int totalUserCount = 0;
        for (Server server : ServerList) {
          int connectedUserCount = SshUtil.getServerConnectedUsers(server);
          totalUserCount += connectedUserCount;
          ClientServerView clientServerView = ClientServerViewList.stream().filter(s -> s.getId() == server.getId()).findAny().orElse(null);
          if (clientServerView == null)
            clientServerView.setConnectedUserCount(0);
          else
            clientServerView.setConnectedUserCount(connectedUserCount);
        }
        for (ClientServerView server : ClientServerViewList) {
          var percent = server.getConnectedUserCount() / totalUserCount * 100;

          for (CongestionLevel congestionLevel : CongestionLevelList) {
            if ((percent >= congestionLevel.getMin()) && (percent <= congestionLevel.getMax())) {
              server.setCongestionLevel(congestionLevel.getName());
            }
          }
        }
        Collections.sort(ClientServerViewList, new Comparator<ClientServerView>() {
          public int compare(ClientServerView o1, ClientServerView o2) {
            if (o1.getConnectedUserCount() == o2.getConnectedUserCount()) {
              return 0;
            } else if (o1.getConnectedUserCount() < o2.getConnectedUserCount()) {
              return 1;
            }
            return -1;
          }
        });
        return ClientServerViewList;
      case "alphabetic":
        return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "hostName"))
                .stream()
                .map(serverViewMapper::toClientView)
                .collect(Collectors.toList());
      case "continental":
        return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "continent"))
                .stream()
                .map(serverViewMapper::toClientView)
                .collect(Collectors.toList());
      case "crypto-friendly":
        return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "hostName"))
                .stream().filter(s -> s.getCryptoFriendly() == 1)
                .map(serverViewMapper::toClientView)
                .collect(Collectors.toList());
      default:
        return ClientServerViewList;
    }
  }

  public Server getServerById(int id) {
    return serverRepository.findById(id)
      .orElseThrow(() -> new NotFoundException(Server.class, id));
  }
}
