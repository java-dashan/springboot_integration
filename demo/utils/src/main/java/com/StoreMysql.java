package com;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StoreMysql {
    public static void main(String[] args) throws Exception{
        String host = "192.168.40.139";
        int port = 22;
        String user = "root";
        String password = "dashan007";
        String command = "./ba.sh";
        String res = exeCommand(host,port,user,password,command);
        System.out.println(res);

//        //远程连接linux
//        String remote = "ssh root@192.168.40.139";
//        String password = "dashan007";
//        //执行文件
//        Runtime runtime = Runtime.getRuntime();
//        Process exec = runtime.exec(remote);
//
//        runtime.exec(password);
//        Process p = runtime.exec("./ba.sh");
//        System.out.println();
    }
    public static String exeCommand(String host, int port, String user, String password, String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String out = IOUtils.toString(in, "UTF-8");

        channelExec.disconnect();
        session.disconnect();

        return out;
    }
}
