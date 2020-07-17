package com.study;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Publish {
    public static void main(String[] args) throws Exception{

        try {
            Connection connection = ConnectionUtils.createConnection();
            Channel channel = connection.createChannel();
            HashMap hashMap = new HashMap();
            hashMap.put("a", 1);
            AMQP.Queue.DeclareOk firstQueue = channel.queueDeclare("firstQueue", false, false, false, hashMap);
            AMQP.Exchange.DeclareOk firstExchange = channel.exchangeDeclare("firstExchange", BuiltinExchangeType.DIRECT);

//            channel.exchangeBind("绑定","")
            AMQP.Queue.BindOk bindOk = channel.queueBind("firstQueue", "firstExchange", "first");
            channel.basicPublish("firstExchange","first",true,null,"第一条消息".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
