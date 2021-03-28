package com.test.java;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandlerTest {
    static class ClassA {
        public void println(String s) {
            System.out.println("classA " + s);
        }
    }

    public static void main(String[] args) throws Throwable {
        Object o = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        getPrint(o).invokeExact("aaa");
    }

    private static MethodHandle getPrint(Object receiver) throws Throwable {
        MethodType methodType = MethodType.methodType(void.class, String.class);
        return MethodHandles.lookup().findVirtual(receiver.getClass(), "println", methodType).bindTo(receiver);
    }
}
