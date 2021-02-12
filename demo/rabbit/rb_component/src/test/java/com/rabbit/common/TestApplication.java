package com.rabbit.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.Unsafe;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestApplication {

    @Autowired
    public RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws JsonProcessingException {
        HashMap<String, String> map = new HashMap<>();
        map.put("aaaa", "a1");
        map.put("bvdsdfas", "b");
        String s = objectMapper.writeValueAsString(map);
        System.out.println(s);
        System.out.println(map);
        rabbitTemplate.convertAndSend("test_exchange","test.abc",s);
    }

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock();
        new ReentrantLock(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                while (!Thread.currentThread().isInterrupted()) {
                }
                lock.unlock();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
            }
        }).start();

        Thread.interrupted();


//        LockSupport.park();
//        LockSupport.unpark(Thread.currentThread());
//        LockSupport.park(Thread.currentThread());
//        LockSupport.parkNanos(1);
//        LockSupport.parkNanos(1,1);
//
//
//
//        try {
//            lock.tryLock(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
//
//        Lock readLock = reentrantReadWriteLock.readLock();
//        Lock writeLock = reentrantReadWriteLock.writeLock();
//
        Condition condition = lock.newCondition();
        condition.await();
//        condition.signal();
//
//
//        Unsafe unsafe = Unsafe.getUnsafe();

    }

}
