package com.rabbit.api;

import com.rabbit.exception.MessageRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author dashan
 * 建造者模式
 * 消息构建
 */
public class MessageBuilder {
    private String messageId;
    private String topic;
    private String routingKey;
    private Map<String, Object> attributes = new HashMap<>();
    private Long delayMills;
    private int messageType = MessageType.CONFIRM;

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder withDelayMills(Long delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withMessageType(int messageType) {
        this.messageType = messageType;
        return this;
    }

    public MessageBuilder withAttributes(Map<String,Object> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

    public MessageBuilder withAttribute(String key,Object value) {
        this.attributes.put(key, value);
        return this;
    }

    public MessageBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public Message build() {
        if (StringUtils.isEmpty(messageId)) {
            messageId = UUID.randomUUID().toString();
        }
        if (StringUtils.isEmpty(topic)) {
            throw new MessageRuntimeException("topic is null");
        }

        return new Message(messageId, topic, routingKey, attributes, delayMills, messageType);
    }

    private MessageBuilder() {

    }
}
