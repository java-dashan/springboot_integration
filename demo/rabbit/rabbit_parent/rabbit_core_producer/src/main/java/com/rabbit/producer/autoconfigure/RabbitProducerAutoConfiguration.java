package com.rabbit.producer.autoconfigure;

import com.rabbit.producer.broker.ProducerClient;
import com.rabbit.producer.broker.RabbitBroker;
import com.rabbit.producer.broker.RabbitBrokerImpl;
import com.rabbit.producer.broker.RabbitTemplateContainer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author da
 */
@Configuration
public class RabbitProducerAutoConfiguration {
    @Bean
    public RabbitBroker rabbitBroker(RabbitTemplateContainer rabbitTemplateContainer) {
        return new RabbitBrokerImpl(rabbitTemplateContainer);
    }
    @Bean
    public ProducerClient producerClient(RabbitBroker rabbitBroker) {
        return new ProducerClient(rabbitBroker);
    }

    @Bean
    public RabbitTemplateContainer rabbitTemplateContainer(ConnectionFactory connectionFactory) {
        return new RabbitTemplateContainer(connectionFactory);
    }
}
