package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);
    private static Selector selector;

    public static void main(String[] args) {
        init();

    }

    public static void init() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            listen(serverSocketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void listen(ServerSocketChannel serverSocketChannel) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                handler(serverSocketChannel,next);
            }
    }

    public static void handler(ServerSocketChannel serverSocketChannel,SelectionKey key) {
        if (key.isAcceptable()) {
            SocketChannel accept = null;
            try {
                accept = serverSocketChannel.accept();
                accept.configureBlocking(false);
                accept.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel)key.channel();
            try {
                channel.read(buffer);
                byte[] array = buffer.array();
                buffer.clear();
                System.out.println(new String(array));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (key.isWritable()) {


        }

    }
}
