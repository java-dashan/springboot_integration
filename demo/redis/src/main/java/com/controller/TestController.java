package com.controller;


import com.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/set")
    public String set(String k, String v, @RequestParam(required = false) Long time){
        if (time == null) {
            redisUtil.vSet(k,v);
        } else {
            redisUtil.vSet(k,v,time);
        }
        return "set success";
    }

    @GetMapping("/get")
    public Object get(String k) {
        return redisUtil.vGet(k);
    }








}
