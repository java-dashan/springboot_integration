package com.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BufferTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\da\\Desktop\\todo.txt"));
            FileChannel channel = fis.getChannel();
            while (channel.read(buffer) != -1) {
                buffer.flip();
                System.out.println(new String(buffer.array()));
//                System.out.println(buffer.position());
//                buffer.position(1);
//                System.out.println(buffer.get());
//                System.out.println(buffer.get(9));
//                System.out.println(buffer.position(10));
//                System.out.println(buffer.hasRemaining());
                buffer.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
