package com.rabbit.api;


import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author da
 */
@Data
public class Message implements Serializable {
    private static final long serialVersionUID = -4808803878437908083L;

    /**
     * 消息唯一id
     */
    private String messageId;

    /**
     * 消息主题 exchange
     */
    private String topic;

    /**
     * 消息的路由规则
     */
    private String routingKey;

    /**
     * 消息附加属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 消息延迟时间
     */
    private Long delayMills;

    /**
     * 消息类型：默认-> 确认消息
     */
    private int messageType = MessageType.CONFIRM;

    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, Long delayMills, int messageType) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
        this.messageType = messageType;
    }

    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, Long delayMills) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
    }

    public Message() {
    }
}
