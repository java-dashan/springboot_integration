package com.thread;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadLocal implements Runnable{
    private ThreadLocal<Integer> local = new ThreadLocal<>();
    private Random random = new Random();

    public static void main(String[] args) {
        TestThreadLocal testThreadLocal = new TestThreadLocal();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(testThreadLocal);
        }
        executorService.shutdown();
    }

    @Override
    public void run() {
        int i = random.nextInt(100000);

        local.set(3);
        local.set(i);


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Integer integer = local.get();
        System.out.println("Thread: " + Thread.currentThread().getName() + " threadLocal: " + integer+", randomValue: "+i);
    }
}
