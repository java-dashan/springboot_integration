package com.redis.lua.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
@ComponentScan("com.redis.lua")
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisTemplate.class)
public class RedisLuaConfiguration {

//    @Bean
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        return new StringRedisTemplate(redisConnectionFactory);
//    }
    @Bean
    public DefaultRedisScript defaultRedisScript() {
        DefaultRedisScript<Boolean> booleanDefaultRedisScript = new DefaultRedisScript<>();
        booleanDefaultRedisScript.setLocation(new ClassPathResource("ratelimiter.lua"));
        booleanDefaultRedisScript.setResultType(Boolean.class);
        return booleanDefaultRedisScript;
    }
}
