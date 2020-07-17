package com.rabbit;


import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = @Queue("myDefault"))
public class MyDefault {
    
    @RabbitHandler
    public void onMessage(String msg) {

        System.out.println("接收到消息"+msg);

    }
}
