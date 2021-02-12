package com.study.instance;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.study.ConnectionUtils;
import com.study.ExchangeEnum;

import java.util.HashMap;
import java.util.Random;

public class Producer {
    public static String EXCHANGE = "secondExchange";
    public static String EXCHANGE_test = "testExchange";
    public static String ERROR_QUEUE = "error";
    public static String INFO_QUEUE = "info";
    public static String WARNING_QUEUE = "warning";

    public static String TEST_QUEUE1 = "TEST1";
    public static String TEST_QUEUE2 = "TEST2";


    public static void main(String[] args) throws Exception{
        Connection connection = ConnectionUtils.createConnection();
        Channel channel = connection.createChannel();
        //声明两个交换机
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(EXCHANGE_test, BuiltinExchangeType.FANOUT);
        //声明五个队列
        channel.queueDeclare(ERROR_QUEUE, false, false, false, null);
        channel.queueDeclare(INFO_QUEUE, false, false, false, null);
        channel.queueDeclare(WARNING_QUEUE, false, false, false, null);
        channel.queueDeclare(TEST_QUEUE1, false, false, false, null);
        channel.queueDeclare(TEST_QUEUE2, false, false, false, null);
        //绑定交换机和队列
        channel.queueBind(INFO_QUEUE, EXCHANGE, "info");
        channel.queueBind(WARNING_QUEUE, EXCHANGE, "warning");
        channel.queueBind(ERROR_QUEUE, EXCHANGE, "error");

        channel.queueBind(TEST_QUEUE1,EXCHANGE_test, "");
        channel.queueBind( TEST_QUEUE2,EXCHANGE_test, "");

        //生产消息
        for (int i = 0; i < 20; i++) {
            String key = randomKey();
            //EXCHANGE
            channel.basicPublish(EXCHANGE,key,false,null,("exchange "+key+" "+i).getBytes());
            //EXCHANGE_test
            channel.basicPublish(EXCHANGE_test,key,false,null,("exchange_test  "+i).getBytes());
        }


    }

    public static String randomKey() {
        String[] ARR = new String[]{"info","warning","error"};
        int i = Math.abs(new Random().nextInt(ARR.length)%3);
        return ARR[i];
    }
}
