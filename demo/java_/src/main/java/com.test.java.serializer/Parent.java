package com.test.java.serializer;

import lombok.Data;

import java.io.Serializable;
@Data
public class Parent implements Serializable {
    private String parentName;
}
