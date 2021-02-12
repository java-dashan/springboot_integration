package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.rabbit.api.Message;
import com.rabbit.api.MessageProducer;
import com.rabbit.api.MessageType;
import com.rabbit.api.SendCallback;
import com.rabbit.exception.MessageRuntimeException;

import java.util.List;

public class ProducerClient implements MessageProducer {

    private RabbitBroker rabbitBroker;

    public ProducerClient(RabbitBroker rabbitBroker) {
        this.rabbitBroker = rabbitBroker;
    }

    public ProducerClient() {
    }

    @Override
    public void send(Message message) throws MessageRuntimeException {
        // 防止使用者不使用 建造者模式创建消息
        Preconditions.checkNotNull(message.getTopic());
        int messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;

        }
    }

    @Override
    public void send(Message message, SendCallback callback) throws MessageRuntimeException {

    }

    @Override
    public void send(List<Message> messageList) throws MessageRuntimeException {

    }
}
