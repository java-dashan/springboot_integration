package com.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class HighRedisLock implements AutoCloseable{

    private RedisTemplate redisTemplate;

    private String key;

    private String value;

    private Long expireTime;

    private static String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
            "\treturn redis.call(\"del\",KEYS[1])\n" +
            "else\n" +
            "   \treturn 0\t\n" +
            "end  ";

    public HighRedisLock(RedisTemplate redisTemplate, String key, Long expireTime) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.value = UUID.randomUUID().toString();
        this.expireTime = expireTime;
    }

    public Boolean getLock() {
        RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
        Expiration expiration = Expiration.seconds(expireTime);
        RedisCallback<Boolean> booleanRedisCallback = new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.set(redisTemplate.getKeySerializer().serialize(key), redisTemplate.getValueSerializer().serialize(value), expiration, setOption);
            }
        };
        return (Boolean) redisTemplate.execute(booleanRedisCallback);
    }

    public Boolean unLock() {
        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        return (Boolean) redisTemplate.execute(redisScript, Arrays.asList(key), value);
    }

    @Override
    public void close() throws Exception {
        unLock();
    }
}
