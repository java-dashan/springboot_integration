package com.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class TestConsumer {

    @KafkaListener(topics = {"json"})
    public void consumer(ConsumerRecord consumerRecord) {
        Optional<Object> value = Optional.ofNullable(consumerRecord.value());
        log.info("接收到消息");
        if (value.isPresent()) {
            Object o = value.get();
            System.out.println("消费消息" + o);
        }
    }
}
