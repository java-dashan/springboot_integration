package com.rabbit.producer.broker;


import com.rabbit.api.Message;

public interface RabbitBroker {
    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessages();
}
