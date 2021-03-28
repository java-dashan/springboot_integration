package com.test.java;


public class ThreadLocalDemo {
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ThreadLocalDemo.threadLocal.set(Thread.currentThread().getName());
                ThreadLocalVariable.say();
            }).start();
        }

    }


}
