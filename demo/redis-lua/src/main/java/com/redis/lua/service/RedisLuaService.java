package com.redis.lua.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisLuaService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisScript<Boolean> redisScript;

    public Boolean limit(String methodKey, Integer limitCount) {
        log.info("key {} limitCount {}", methodKey, limitCount);
        Boolean result = false;
        result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(methodKey), limitCount.toString());
        if (!result) {
            throw new RuntimeException("more than limit" + limitCount);
        }
        return result;
    }
}
