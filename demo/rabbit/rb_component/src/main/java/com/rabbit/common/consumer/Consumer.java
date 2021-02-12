package com.rabbit.common.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class Consumer {

    private ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "test_queue")
    public void onMessgae(Channel channel, Message message) {
        try {
            HashMap hashMap = objectMapper.readValue(message.getBody(), HashMap.class);
            for (Object o : hashMap.entrySet()) {
                System.out.println(o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
