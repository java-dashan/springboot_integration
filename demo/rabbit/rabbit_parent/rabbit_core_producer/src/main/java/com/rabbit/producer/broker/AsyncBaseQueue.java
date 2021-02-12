package com.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.util.concurrent.*;


@Slf4j
public class AsyncBaseQueue {

    /**
     * 线程池大小为CPU核心个数
     */
    private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int QUEUE_SIZE = 1000;

    private static ExecutorService senderAsync = new ThreadPoolExecutor(
            THREAD_SIZE,
            THREAD_SIZE * 2,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_SIZE), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("rabbit_sender");
            return thread;
        }
    },
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.error("async is  rejected,runnable: {},executor", r, executor);
                }
            });

    public static void submit(Runnable runnable) {
        senderAsync.submit(runnable);
    }
}
