package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.exception.MessageRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {
    /**
     * 池化: 因为rabbitTemplate为单例对象，不同消息类型需要对应不同的 rabbitTemplate ，提供多个rabbitTemplate提高性能
     * k -> exchange ,V-> rabbitTemplate
     */
    private Map<String, RabbitTemplate> container = Maps.newConcurrentMap();

    private ConnectionFactory connectionFactory;

    public RabbitTemplateContainer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public RabbitTemplateContainer() {

    }

    public RabbitTemplate get(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = container.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        log.info("#RabbitTemplateContainer.get#  template is not exist,create a new template -> topic:{}", topic);
        RabbitTemplate newRabbitTemplate = new RabbitTemplate(connectionFactory);
        newRabbitTemplate.setExchange(topic);
        newRabbitTemplate.setRoutingKey(message.getRoutingKey());
        newRabbitTemplate.setRetryTemplate(new RetryTemplate());
//        不是迅速消息都得确认消息
        if (message.getMessageType() != MessageType.RAPID) {
            newRabbitTemplate.setConfirmCallback(this);
        }
//        事务消息  一般不选择，性能太慢
//        rabbitTemplate.setChannelTransacted(true);

//        序列化
//        newRabbitTemplate.setMessageConverter();

//        如果不存在则put
        container.putIfAbsent(topic, newRabbitTemplate);
        return newRabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // todo 可以把这个移到Message中 提高可扩展性
    }
}
