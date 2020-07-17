package com.study;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ConnectionUtils {

    public static Connection createConnection() throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("5672");
        connectionFactory.setHost("localhost");
        return connectionFactory.newConnection();
    }
}
