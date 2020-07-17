package com.rabbit;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareBatchMessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RabbitListeners(
        {
                @RabbitListener(bindings = @QueueBinding(
                        value = @Queue("queue3"),
                        exchange = @Exchange(value = "myFanout", type = ExchangeTypes.FANOUT)
//                        key = "key.one"
                )),
                @RabbitListener(bindings = @QueueBinding(
                        value = @Queue("queue4"),
                        exchange = @Exchange(value = "myFanout",type = ExchangeTypes.FANOUT)
//                        key = "key.two"
                ))
        }
)
public class MyFanout implements ChannelAwareMessageListener {

    @RabbitHandler
    public void onMessage(@Payload String message, @Headers Map<String, Object> map) {
        System.out.println("来自 "+map.get(AmqpHeaders.CONSUMER_QUEUE) +" 的消息： "+message );
        Integer integer = (Integer)map.get(AmqpHeaders.DELIVERY_TAG);
    }

    @RabbitHandler
    public void onMessage1(Message message) {
//        message.getMessageProperties().get
//        DeliveryTag()
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);

    }
}
