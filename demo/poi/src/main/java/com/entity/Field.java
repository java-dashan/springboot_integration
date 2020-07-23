package com.entity;


import lombok.Data;

@Data
public class Field {
    private String fieldName;
    private String fieldType;
    private String fieldLong;
    private String defaultValue;
    private String fieldComment;

    @Override
    public String toString() {
        return "\n"+fieldName+" "+fieldType.toLowerCase()+"("+fieldLong + ")" + " comment " +"'"+fieldComment+"'";
//                "fieldName='" + fieldName + '\'' +
//                ", fieldType='" + fieldType + '\'' +
//                ", fieldLong='" + fieldLong + '\'' +
//                ", defaultValue='" + defaultValue + '\'' +
//                ", fieldComment='" + fieldComment + '\'' +
//                ')';
    }
}
