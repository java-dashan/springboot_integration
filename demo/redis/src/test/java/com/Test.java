package com;


import com.util.RedisUtil;
import com.util.SpringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class Test {
    
    @Autowired
    RedisUtil redisUtil;
    
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SpringUtils springUtils;
    
    @org.junit.Test
    public void test1(){
        //set结构的  get set
        redisTemplate.opsForSet().add("k", "k1", "k2", "k3");
        Set k = redisTemplate.opsForSet().members("k");
        Iterator iterator = k.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    @org.junit.Test
    public void test2(){
        //set isMember判断 数据是否在set内
        System.out.println(redisTemplate.opsForSet().isMember("k", "k1"));
        System.out.println(redisTemplate.opsForSet().isMember("k", "k4"));
        redisTemplate.opsForSet().remove("k", "k1");
        System.out.println(redisTemplate.opsForSet().isMember("k", "k1"));
    }

    @org.junit.Test
    public void test3(){
        //测试list结构的增删
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("list", "1");
        listOperations.leftPush("list", "2");
        List list = listOperations.range("list", 0, -1);
        System.out.println(list);
        System.out.println(list.contains("1"));
    }

    @org.junit.Test
    public void test4(){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zset", 1, 1);
        zSetOperations.add("zset", 2, 1);
        zSetOperations.add("zset", 3, 1);
        zSetOperations.add("zset", 1, 1);
        Set zset = zSetOperations.range("zset", 0, -1);
        Iterator iterator = zset.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    @org.junit.Test
    public void testHash(){
        redisUtil.hSet("hash", "k", "v");
        redisUtil.hSet("hash", "k", "v1");
        redisUtil.hSet("hash", "k2", "v2");
        redisUtil.hSet("hash", "k3", "v3");
        System.out.println(redisUtil.hGetMap("hash"));
    }

    @org.junit.Test
    public void testHash1(){
        Map map = new HashMap();
        map.put("k4", "v4");
        map.put("k5", "v5");
        map.put("k6", "v6");
        redisUtil.hPutAll("hash",map);
//        redisUtil.expire("hash", 30000);
        System.out.println(redisUtil.hget("hash","k6"));
        System.out.println(redisUtil.hGetMap("hash"));
        System.out.println(redisTemplate.getExpire("hash"));
    }


    @org.junit.Test
    public void test6(){
        Object bean = SpringUtils.getBean("redisTemplate",RedisTemplate.class);
        System.out.println(((RedisTemplate) bean).getExpire("hash"));
    }

}
