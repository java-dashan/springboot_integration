package com.rabbit.api;

import com.rabbit.exception.MessageRuntimeException;

import java.util.List;

public interface MessageProducer {

    /**
     * 消息发送 -> 回调
     * @param message
     * @throws MessageRuntimeException
     */
    void send(Message message) throws MessageRuntimeException;

    /**
     * 消息发送
     * @param message
     * @throws MessageRuntimeException
     */
    void send(Message message,SendCallback callback) throws MessageRuntimeException;

    /**
     * 批量发送
     * @param messageList
     * @throws MessageRuntimeException
     */
    void send(List<Message> messageList) throws MessageRuntimeException;


}
