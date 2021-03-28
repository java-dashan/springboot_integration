package com.test.java;


import java.util.Vector;

public class VectorDemo {

    static Vector<Integer> vector = new Vector<>();
    static int a = 10;

    public static void main(String[] args) {
        while (a>0) {
            for (int i = 0; i < 10; i++) {
                vector.add(i);
            }

            Thread thread = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    vector.remove(i);
                }
            });

            Thread thread1 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    System.out.println(vector.get(i));
                }
            });
            thread.start();

            thread1.start();

            a--;
        }
    }
}
