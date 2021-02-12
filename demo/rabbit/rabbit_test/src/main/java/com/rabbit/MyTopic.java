package com.rabbit;


import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class MyTopic {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("province-news-queue"),
            exchange = @Exchange(value = "myTopic",type = ExchangeTypes.TOPIC),
            key = "province.#"
    ))
    @RabbitHandler
    public void province(String msg) {
        System.out.println("省级通告新闻： "+msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("city-news-queue"),
            exchange = @Exchange(value = "myTopic",type = ExchangeTypes.TOPIC),
            key = "province.city.#"
    ))
    @RabbitHandler
    public void city(String msg) {
        System.out.println("市级通告新闻： "+msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("street-news-queue"),
            exchange = @Exchange(value = "myTopic",type = ExchangeTypes.TOPIC),
            key = "province.city.street.*"
    ))
    @RabbitHandler
    public void street(String msg, Message message) {
//        String consumerQueue = message.getMessageProperties().getConsumerQueue();
//        MessageDeliveryMode deliveryMode = message.getMessageProperties().getDeliveryMode();
//        deliveryMode.
        System.out.println("街道通告新闻： "+msg);
    }
    
}
