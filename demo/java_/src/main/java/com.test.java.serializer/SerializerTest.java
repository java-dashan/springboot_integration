package com.test.java.serializer;

import java.io.*;

public class SerializerTest {
    public static void main(String[] args) {
        String file = "C:\\Users\\da\\Desktop\\test 3.txt";

        User user = new User();
        user.setParentName("da");
        user.setName("dashan");
        user.setAge(18);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(user);
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            User o = (User)inputStream.readObject();
            System.out.println(o.getParentName());
            System.out.println(o);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
