package com.service;


import org.springframework.stereotype.Service;

@Service
public class RecordService {

    private Integer value = 5;

    public Integer getValue() {
        return this.value;
    }
}
