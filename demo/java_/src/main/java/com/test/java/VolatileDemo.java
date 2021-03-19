package com.test.java;

import lombok.Data;

public class VolatileDemo {

    public static void main(String[] args) {
        Test test = new Test();

        Thread thread1=new Thread(() -> {
            System.out.println(test.getA());
        });
        Thread thread=new Thread(() -> {
            test.setA(1);
        });
        thread1.start();
        thread.start();
    }
    @Data
    static class Test{
        int a =0;
    }
}
