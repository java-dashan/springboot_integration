package com.util;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RedisLock {

    private static RedisTemplate redisTemplate;

    private static String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
            "\treturn redis.call(\"del\",KEYS[1])\n" +
            "else\n" +
            "   \treturn 0\t\n" +
            "end  ";

    public RedisLock(RedisTemplate redisTemplate) {
        RedisLock.redisTemplate = redisTemplate;
    }

    public static Boolean getLock(String key, String value, Long expireTime) {
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

    public static Boolean unLock(String key, String value) {
        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        return (Boolean) redisTemplate.execute(redisScript, Arrays.asList(key), value);
    }
}
