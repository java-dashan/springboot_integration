package com.study.instance.consumer;

import com.rabbitmq.client.*;
import com.study.ConnectionUtils;
import com.study.instance.Producer;

import java.io.IOException;

public class TestQueue2 {
    public static void  main(String [] args){
        try {
            Connection connection = ConnectionUtils.createConnection();
            Channel channel = connection.createChannel();
            channel.basicConsume(Producer.TEST_QUEUE1,false,new DefaultConsumer(channel){
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String s = new String(body);
                    System.out.println(s);
//                    channel.basicAck(envelope.getDeliveryTag(),false);
                    channel.basicReject(envelope.getDeliveryTag(),true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
