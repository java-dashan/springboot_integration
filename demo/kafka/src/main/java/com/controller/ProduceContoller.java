package com.controller;

import com.entity.TestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produce")
public class ProduceContoller {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

//    @GetMapping("/send")
//    public String send(TestEntity entity) {
//        kafkaTemplate.send("json", entity.toString());
//        return "success";
//    }

    @GetMapping("/send1")
    public String send1(TestEntity entity) {
        kafkaTemplate.send("object", entity.toString());
        return "success";
    }
}
