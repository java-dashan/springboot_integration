package com.redis.lua.anno;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LuaLimit {
    int limitCount() default 1;
}
