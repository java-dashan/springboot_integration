package com.rabbit.producer.broker;

import com.rabbit.api.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    private RabbitTemplateContainer rabbitTemplateContainer;

    public RabbitBrokerImpl(RabbitTemplateContainer rabbitTemplateContainer) {
        this.rabbitTemplateContainer = rabbitTemplateContainer;
    }

    public RabbitBrokerImpl() {
    }

    @Override
    public void rapidSend(Message message) {
        sendKernel(message);
    }

    /**
     * 发送消息的核心方法
     * 异步发送消息提高性能
     * @param message
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.submit(()->{
            CorrelationData correlationData = new CorrelationData(
                    String.format("%s#%s", message.getMessageId(), System.currentTimeMillis())
            );
            String exchange = message.getTopic();
            String routingKey = message.getRoutingKey();
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.get(message);
//            rabbitTemplate.set
            rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq,messageId:{}",correlationData.getId());
        });
    }

    @Override
    public void confirmSend(Message message) {

    }

    @Override
    public void reliantSend(Message message) {

    }

    @Override
    public void sendMessages() {

    }
}
