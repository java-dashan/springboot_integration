package com.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;

@Data
public class User {
    private String name;
    private String id;

    @Override
    @PostMapping
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
