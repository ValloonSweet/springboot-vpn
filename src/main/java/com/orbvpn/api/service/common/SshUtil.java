package com.orbvpn.api.service.common;

import com.jcraft.jsch.*;

import java.io.ByteArrayOutputStream;

public class SshUtil {
    public static String executeCommandUsingPss(String username, String sshPassword, String host, int port,
                                                String command) throws JSchException, InterruptedException {
        return executeCommand(username, sshPassword, null, host, port,
                null, null, command);
    }

    public static String executeCommandUsingPrivateKey(String username, String prvKeyAbsolutePath, String host, int port,
                                                       String command) throws JSchException, InterruptedException {
        return executeCommand(username, null, prvKeyAbsolutePath, host, port,
                null, null, command);
    }

    /**
     * this method is currently used for Cisco servers
     */
    public static String executeCommandUsingPss(String username, String sshPass, String host, int port,
                                                String rootCommand, String rootPassword, String command)
            throws JSchException, InterruptedException {
        return executeCommand(username, sshPass, null, host, port, rootCommand, rootPassword, command);
    }


    private static String executeCommand(String username, String sshPassword, String prvKeyAbsolutePath, String host,
                                         int port, String rootCommand, String rootPassword, String mainCommand)
            throws JSchException, InterruptedException {

        if(sshPassword != null && prvKeyAbsolutePath != null){
            throw new RuntimeException("both ssh key and private key are provid");
        } else if(sshPassword == null && prvKeyAbsolutePath == null){
            throw new RuntimeException("non of ssh key and private key are provid");
        }

        Session session = null;
        ChannelExec channel = null;

        try {
            JSch jSch = new JSch();
            if (prvKeyAbsolutePath != null) {
                jSch.addIdentity(prvKeyAbsolutePath);
            }
            session = jSch.getSession(username, host, port);
            if (sshPassword != null) {
                session.setPassword(sshPassword);
            }
            // todo http://stackoverflow.com/questions/30178936/jsch-sftp-security-with-session-setconfigstricthostkeychecking-no
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            String channelType = "exec";
            channel = (ChannelExec) session.openChannel(channelType);
            //channel.setErrStream(System.err);
            //channel.setPty(true);
            if(rootCommand != null && rootPassword != null){
                channel.setCommand(rootCommand);
                Thread.sleep(1000);
                channel.setCommand(rootPassword);
                Thread.sleep(1000);
            }

            channel.setCommand(mainCommand);
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
