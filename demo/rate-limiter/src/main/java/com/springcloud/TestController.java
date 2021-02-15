package com.springcloud;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class TestController {
    /**
     * 每秒生成2.0个令牌
     */
    RateLimiter rateLimiter = RateLimiter.create(2.0);

    @GetMapping("/tryAcquire")
    public Boolean tryAcquire(Integer count) {
        if (rateLimiter.tryAcquire(count)) {
            log.info("获取到令牌");
            return Boolean.TRUE;
        }
        log.info("未获取到令牌");
        return Boolean.FALSE;
    }

    /**
     * 带尝试时间的获取
     * @param count
     * @param timeout
     * @return
     */
    @GetMapping("tryAcquireWithTimeout")
    public Boolean tryAcquireWithTimeout(Integer count,Integer timeout) {
        if (rateLimiter.tryAcquire(count,timeout, TimeUnit.SECONDS)) {
            log.info("获取到令牌");
            return Boolean.TRUE;
        }
        log.info("未获取到令牌");
        return Boolean.FALSE;
    }

    @GetMapping("/acquire")
    public Boolean acquire(Integer count){
        rateLimiter.acquire(count);
        log.info("获取令牌成功");
        return Boolean.TRUE;
    }
}
