package com.test.java;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Date;

public class DirectMemoryDemo {
    public static void main(String[] args) throws Exception{
        Field field = Unsafe.class.getDeclaredFields()[0];
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        while (true) {
            unsafe.allocateMemory(1024 * 1024);
        }
    }
}
