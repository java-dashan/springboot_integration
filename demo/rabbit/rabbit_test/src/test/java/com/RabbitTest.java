package com;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void test() {
        System.out.println(rabbitTemplate);
        for (int i = 0; i < 15; i++) {
            rabbitTemplate.convertAndSend("ackQueue","aaa");
        }
    }

    @Test
    public void test1() {
        System.out.println(rabbitTemplate);
        for (int i = 0; i < 15; i++) {
            rabbitTemplate.convertAndSend("ackQueue1","aaa");
        }
    }
}
