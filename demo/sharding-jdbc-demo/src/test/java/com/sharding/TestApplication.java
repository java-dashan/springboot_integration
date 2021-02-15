package com.sharding;

import com.sharding.dao.OrderMapper;
import com.sharding.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestApplication {
    @Autowired
    OrderMapper orderMapper;

    @Test
    public void test() {
        Order order = new Order();

        order.setSpuId(20L);
        order.setId(5L);
        order.setCreateBy("da");
        order.setCreateTime(new Date());
        orderMapper.insert(order);
    }


    @Test
    public void testQuery() {
        Order order = orderMapper.selectByPrimaryKey(5L);
        System.out.println(order);
    }
}
