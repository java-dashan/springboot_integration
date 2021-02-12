package com.rabbit.api;


/**
 * 消息监听
 */
public interface MessageListener {

    void onMessage(Message message);
}
