package com.rabbit.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class AckConsumer {

    @RabbitListener(queues = "ackQueue")
    public void onMessage(Channel channel, Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("DeliveryMode: {%s}, ConsumerQueue: %s, DeliveryTag: %s, CorrelationId: %s "+" "+
                messageProperties.getDeliveryMode()+" "+
                messageProperties.getConsumerQueue()+" "+
                messageProperties.getDeliveryTag()+" "+
                messageProperties.getCorrelationId());
        System.out.println(" 当前时间为： "+new Date()+" message = "+new String(message.getBody()));
        try {
            if (System.currentTimeMillis() % 2 == 0) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
