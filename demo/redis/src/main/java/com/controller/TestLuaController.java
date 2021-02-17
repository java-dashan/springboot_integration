package com.controller;

import com.redis.lua.anno.LuaLimit;
import com.redis.lua.service.RedisLuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLuaController {
    @Autowired
    private RedisLuaService redisLuaService;

    @GetMapping("/testLua")
    @LuaLimit(limitCount = 1)
    public String testLua() {
        return "success";
    }
}
