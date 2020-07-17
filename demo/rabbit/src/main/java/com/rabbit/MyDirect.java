package com.rabbit;


import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@RabbitListener(bindings = @QueueBinding(
        value = @Queue("queue1"),
        exchange = @Exchange(value = "myDirect",type = ExchangeTypes.DIRECT),
        key = "direct"
))
@Component
public class MyDirect {

    @RabbitHandler
    public void onMessage(String msg) {
        System.out.println("direct: "+msg);
    }
}
