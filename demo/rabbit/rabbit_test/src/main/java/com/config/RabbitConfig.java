package com.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

//    @Autowired
//    RabbitTemplate rabbitTemplate;

    @Bean
    Queue ackQueue() {
        return new Queue("ackQueue");
    }

    @Bean
//    @Autowired
    String hello(RabbitTemplate rabbitTemplate) {
        //消息发送失败返回队列,yml需要配置 publish-returns = true
        rabbitTemplate.setMandatory(true);


        // yml publish-returns = true
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationId();
            System.out.println(correlationId);
//            log.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
        });

        //消息确认 publish-confirms: true
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//                    String correlationId = correlationData.getReturnedMessage().getMessageProperties().getCorrelationId();
//                    System.out.println(correlationId);
                    System.out.println(ack);
                }
        );

        return "hello";
    }

}
