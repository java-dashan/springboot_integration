package com.rabbit.api;

import lombok.Data;


/**
 * 消息类型
 */
@Data
public final class MessageType {
    /**
     * 快速消息：不需要保证消息可靠性，也不需要确认
     */
    public static final int RAPID = 0;

    /**
     * 确认消息：不需要保证消息可靠性，但会做确认 ->  callback
     */
    public static final int CONFIRM = 1;

    /**
     * 可靠性消息：必须保证百分百投递成功，不允许丢失
     * ps：保证数据库和消息投递的一致性（最终一致性）
     */
    public static final int RELIANT = 2;


}
