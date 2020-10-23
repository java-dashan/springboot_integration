package com.test.java.serializer;

import lombok.Data;


@Data
public class User extends Parent{
    private String name;
    private Integer age;
}
