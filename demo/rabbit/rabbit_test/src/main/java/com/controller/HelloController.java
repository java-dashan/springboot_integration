package com.controller;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class HelloController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/default")
    public String sendToDefault(String msg){
        // default routingkey 指绑定queue名称
        rabbitTemplate.convertAndSend("myDefault","default:  "+msg);

//        rabbitTemplate.
        return "sendToDefault success";
    }

    @GetMapping("/direct")
    public String sendToDirect(String msg){
        rabbitTemplate.convertAndSend("myDirect","direct","direct:  "+msg);
        return "sendToDirect success";
    }

    @GetMapping("/fanout")
    public String sendToFanout(String msg) {
        //fanout routingkey无关紧要  可以随便
        rabbitTemplate.convertAndSend("myFanout","","fanout:  "+msg);
//        rabbitTemplate.convertAndSend("myFanout","key.one","fanout:  "+msg);
        return "sendToFanout success";
    }

    @GetMapping("/topic")
    public String sendToTopic(String msg) {
        //模拟彩票中奖各级公告
        rabbitTemplate.convertAndSend("myTopic","province.city.street.shop","topic:  "+msg);
        return "sendToTopic success";
    }

    @GetMapping("/")
    public String get(){
        return "hello";
    }
}
