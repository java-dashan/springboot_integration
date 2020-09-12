package com.controller;


import com.entity.User;
import com.service.EhcacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private EhcacheService ehcacheService;

    @GetMapping("/user")
    public User get(String username){
        return ehcacheService.findByUsername(username);
    }

//    @GetMapping("/")
//    public String get(){
//        return "hello";
//    }

}
