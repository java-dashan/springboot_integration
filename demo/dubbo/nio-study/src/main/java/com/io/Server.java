package com.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static ExecutorService executorService = Executors.newCachedThreadPool();
    static ServerSocket serverSocket = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                Socket accept = serverSocket.accept();
                executorService.execute(new SocketThread(accept));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
