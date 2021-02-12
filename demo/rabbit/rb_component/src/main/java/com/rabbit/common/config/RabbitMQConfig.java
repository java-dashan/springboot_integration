package com.rabbit.common.config;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

//    @Bean
//    public Queue queue() {
//        return new Queue("test_queue", true, true, false);
//    }
//
//    @Bean
//    public Binding binding() {
//        return new Binding("绑定test_queue到 test_exchange", null, "test_exchange", "test.*",null);
//    }
//
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange("test_exchange", true, false);
//    }



}
