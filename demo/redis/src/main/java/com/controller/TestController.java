package com.controller;


import com.util.HighRedisLock;
import com.util.RedisLock;
import com.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisScriptingCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Redisson redisson;

    @GetMapping("/set")
    public String set(String k, String v, @RequestParam(required = false) Long time) {
        if (time == null) {
            redisUtil.vSet(k, v);
        } else {
            redisUtil.vSet(k, v, time);
        }
        return "set success";
    }

    @GetMapping("/get")
    public Object get(String k) {
        return redisUtil.vGet(k);
    }


    @GetMapping("/redissonLock")
    public String redissonLock() {
        log.info("进入了方法");

        RLock lock = redisson.getLock("redissonLock");
        try {
            lock.lock(30, TimeUnit.SECONDS);
            Thread.sleep(10000);
            System.out.println("我是你大哥");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "";
    }

    private RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();

    private Expiration expiration = Expiration.seconds(30);

    @GetMapping("/lock")
    public String lock() {
        log.info("进入了方法");
        String key = "lock";
        String uuid = UUID.randomUUID().toString();
        Boolean flag = (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.set(redisTemplate.getKeySerializer().serialize(key), redisTemplate.getValueSerializer().serialize(uuid), expiration, setOption);
            }
        });
        if (flag) {
            System.out.println("获取到锁");
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "\treturn redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "   \treturn 0\t\n" +
                        "end  ";
                RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);

                Boolean result = (Boolean) redisTemplate.execute(redisScript, Arrays.asList(key), uuid);
                log.info("释放锁结果" + result);
            }
        }
        return "程序结束";
    }


    @GetMapping("/highRedisLock")
    public String highRedisLock() {
        log.info("进入了方法");
        try (HighRedisLock redisLock = new HighRedisLock(redisTemplate, "highRedisLock", 30L)) {
            if (redisLock.getLock()) {
                log.info("进入了锁");
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法执行完成");
        return "程序结束";
    }

    @GetMapping("/redisLock")
    public String redisLock() {
        log.info("进入了方法");
        String key = "redisLock";
        String uuid = UUID.randomUUID().toString();
        try {
            if (RedisLock.getLock(key, uuid, 30L)) {
                log.info("进入了锁");
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            RedisLock.unLock(key, uuid);
        }
        log.info("方法执行完成");
        return "程序结束";
    }


}
