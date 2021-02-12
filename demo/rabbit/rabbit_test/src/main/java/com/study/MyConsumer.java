package com.study;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class MyConsumer {
    public static void main(String[] args) throws Exception{

        try {
            Connection connection = ConnectionUtils.createConnection();
            Channel channel = connection.createChannel();
            GetResponse firstQueue = channel.basicGet("firstQueue", true);
            System.out.println(new String(firstQueue.getBody()));
//            channel.basicAck(firstQueue.getEnvelope().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
