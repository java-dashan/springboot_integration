package com.test.java;

public class Synchronizedemo {
    synchronized static void test() {
        System.out.println("进入");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(111);
    }

    synchronized void test1() {
        System.out.println("进入1");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
//                Thread.currentThread().interrupt();
            }
            System.out.println(222);
        }
    }

    public static void main(String[] args) {
        Synchronizedemo synchronizedemo = new Synchronizedemo();
        Thread thread = new Thread(() -> synchronizedemo.test1());
        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}
