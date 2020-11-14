package com.test.java;

public class Synchronizedemo {

    synchronized static void test() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(111);
    }

    synchronized void test1() {
        System.out.println(222);
    }

    public static void main(String[] args) {
        Synchronizedemo synchronizedemo = new Synchronizedemo();
        new Thread(() -> Synchronizedemo.test()).start();
        new Thread(() -> synchronizedemo.test1()).start();
    }
}
