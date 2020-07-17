package com.study;

public enum ExchangeEnum {
    DIRECT("DIRECT"),
    FANOUT("FANOUT"),
    TOPIC("TOPIC");

    private String type = "DIRECT";
    ExchangeEnum(String type) {
        this.type = type;
    }

    ExchangeEnum() {

    }

    public static String DIRECT() {
        return DIRECT.type;
    }
    public static String FANOUT() {
        return FANOUT.type;
    }
    public static String TOPIC() {
        return TOPIC.type;
    }



}
