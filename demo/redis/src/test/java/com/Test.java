package com;


import com.google.common.collect.Lists;
import com.util.RedisUtil;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
@Slf4j
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
    
    @org.junit.Test
    public void test7() {
        redisTemplate.opsForValue().set("a", "a", 20, TimeUnit.SECONDS);
        Long a = redisTemplate.getExpire("a", TimeUnit.SECONDS);
        System.out.println(a);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long a1 = redisTemplate.getExpire("a", TimeUnit.SECONDS);
        System.out.println(a1);
    }

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @org.junit.Test
    public void testSet() {
        redisUtil.sSet("set", "aaa", "bbb", "ccc");
        System.out.println(redisUtil.sGet("set"));
        System.out.println(redisUtil.isExistSet("set", "aaa"));
        redisUtil.sRemove("set", "aaa");
        System.out.println(redisUtil.isExistSet("set", "aaa"));

        //        1.executePipelined 重写 入参 RedisCallback 的doInRedis方法
        List<Object> resultList = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {

            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                2.connection 打开管道
                connection.openPipeline();

//                3.connection 给本次管道内添加 要一次性执行的多条命令

//                3.1 一个set操作
                byte[] key1 = "mykey1".getBytes();
                byte[] value1 = "字符串value".getBytes();
                connection.set(key1,value1);

//                3.2一个批量mset操作
                Map<byte[],byte[]> tuple = new HashMap<>();
                tuple.put("m_mykey1".getBytes(),"m_value1".getBytes());
                tuple.put("m_mykey2".getBytes(),"m_value2".getBytes());
                tuple.put("m_mykey3".getBytes(),"m_value3".getBytes());
                connection.mSet(tuple);

//                 3.3一个get操作
                connection.get("m_mykey1".getBytes());
                connection.get("m_mykey2".getBytes());
                connection.get("m_mykey3".getBytes());

                connection.sAdd("mmm".getBytes(), "m".getBytes(), "mm".getBytes(), "mmm".getBytes());
                connection.sMembers("mmm".getBytes());

//                4.关闭管道 不需要close 否则拿不到返回值
//                connection.closePipeline();

//                这里一定要返回null，最终pipeline的执行结果，才会返回给最外层
                return null;
            }
        });
        log.info("{}",resultList);
    }

    @org.junit.Test
    public void testSadd() {
        ArrayList<String> objects = Lists.newArrayList();
        objects.add("a");
        objects.add("b");
        objects.add("c");
        objects.add("c");
        redisUtil.sSet("list1", objects.toArray());
        System.out.println(redisUtil.sGet("list1"));
        System.out.println(redisUtil.isExistSet("list1", "a"));
    }

}
