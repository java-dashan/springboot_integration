package com.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> template;

    //普通
    public void vSet(String k, Object v) {
        template.opsForValue().set(k, v,0);
    }

    public void vSet(String k, Object v,long time) {
        template.opsForValue().set(k, v,time, TimeUnit.SECONDS);
    }

    public Object vGet(String k) {
        System.out.println(template.getExpire(k));
        if (template.getExpire(k)==(-2)) {
            return "已过期";
        }
        return template.opsForValue().get(k);
    }
    //set
    public void sSet(String key, Object... objs) {
        template.opsForSet().add(key, objs);
    }

    public Set<Object> sGet(String k) {
        return template.opsForSet().members(k);
    }

    //设置过期时间
    public Boolean expire(String key, long time) {
        Boolean expire = this.expire0(key,time,TimeUnit.SECONDS);
        return expire;
    }

    public long getExpire(String key) {
        return template.getExpire(key);
    }

    public Boolean expire0(String key, long time, TimeUnit time_type) {
        return template.expire(key, time, time_type);
    }

    //hash
    public void hSet(String key, Object k, Object v) {
        template.opsForHash().put(key, k, v);
    }

    public Map<Object, Object> hGetMap(String key) {
        return template.opsForHash().entries(key);
    }

    public Object hget(String key, Object k) {
        return template.opsForHash().get(key, k);
    }

    public void hPutAll(String key, Map<Object, Object> map) {
        template.opsForHash().putAll(key,map);
    }

    //list
    public void lset(String key, Object v) {
        template.opsForList().leftPush(key, v);
    }

    public void lsetAll(String key, Collection collection) {
        template.opsForList().leftPushAll(key, collection);
    }

    public List lget(String key) {
        return template.opsForList().range(key, 0, -1);
    }

    //zset
    public void zset(String key, Object o, double score) {
        template.opsForZSet().add(key, o, score);
    }

//    public Object zGet(String key) {
//        template.opsForZSet().score();
//    }


}
