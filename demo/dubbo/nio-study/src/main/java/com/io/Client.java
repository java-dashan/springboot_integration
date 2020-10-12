package com.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost",8080);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            User user = new User();
            user.setAge(18);
            user.setName("dashan");
            objectOutputStream.writeObject(user);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            User o = (User) objectInputStream.readObject();
            System.out.println(o);
//            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
