package com;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class Test {


    @Autowired
    private KafkaTemplate kafkaTemplate;

    @org.junit.Test
    public void t1() {
        kafkaTemplate.send("ooo",  "woshibbb");
    }




}
