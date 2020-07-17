package com.study.instance.consumer;

import com.rabbitmq.client.*;
import com.study.ConnectionUtils;
import com.study.instance.Producer;

import java.io.IOException;

public class InfoConsumer {
    public static void main(String[] args) throws Exception{
        Connection connection = ConnectionUtils.createConnection();
        Channel channel = connection.createChannel();
        channel.basicConsume(Producer.INFO_QUEUE, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String s = new String(body);
                //("exchange "+key+" "+i)
                System.out.println(s);
                if (Integer.parseInt(s.substring(s.length() - 1, s.length())) % 2 == 0) {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } else {
                    channel.basicReject(envelope.getDeliveryTag(),false);
                }

            }
        });
    }
}
