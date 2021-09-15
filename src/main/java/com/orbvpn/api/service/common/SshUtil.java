package com.orbvpn.api.service.common;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;

@Slf4j
public class SshUtil {
    public static String executeCommandUsingPss(String username, String password,
                                                String host, int port, String command) throws JSchException, InterruptedException {
        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            String channelType = "exec";
            channel = (ChannelExec) session.openChannel(channelType);
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }
            return responseStream.toString();
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public static String executeCommandUsingPrivateKey(String username, String prvKeyAbsolutePath,
                                                       String host, int port, String command) throws JSchException, InterruptedException {
        Session session = null;
        ChannelExec channel = null;

        try {
            JSch jSch = new JSch();
            jSch.addIdentity(prvKeyAbsolutePath);
            session = jSch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            return responseStream.toString();
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public static void sendingFileUsingPrivateKey(String username, String prvKeyFileName,
                                                  String host, int port, String srcFileName, String desFileName) throws Exception {
        Session session = null;
        Channel channel = null;
        try {
            JSch jSch = new JSch();
            jSch.addIdentity(prvKeyFileName);
            session = jSch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = session.openChannel("sftp");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();
            System.out.println("shell channel connected....");

            ChannelSftp c = (ChannelSftp) channel;
            c.put(srcFileName, desFileName);//"./in/"
            c.exit();

        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}
