package com.controller;

import com.util.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private CuratorFramework client;

    @GetMapping("/curator")
    public String curator() {
        String lockPath = "/lock";
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);
        try {
            if (lock.acquire(10, TimeUnit.SECONDS)) {
                log.info("获取到锁");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("释放锁");
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "执行完毕";
    }

    @GetMapping("/lock")
    public String lock() throws IOException {
        log.info("进入方法");
        try(ZkLock zkLock = new ZkLock()) {
            if (zkLock.getLock("order")) {
                log.info("我获取到锁");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("执行完毕");
        return "";
    }

}
