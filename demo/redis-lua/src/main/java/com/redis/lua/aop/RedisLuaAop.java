package com.redis.lua.aop;

import com.redis.lua.anno.LuaLimit;
import com.redis.lua.service.RedisLuaService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class RedisLuaAop {

    @Autowired
    private RedisLuaService redisLuaService;

    @Value("${lua.limit:1}")
    private Integer limitCount;

    @Pointcut("@annotation(com.redis.lua.anno.LuaLimit)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void beforePointCut(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LuaLimit annotation = method.getAnnotation(LuaLimit.class);
        redisLuaService.limit(method.getName(), annotation.limitCount() == 0 ? 0 : limitCount);
    }
}
