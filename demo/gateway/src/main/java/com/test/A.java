package com.test;

public class A {
    int a = 10;

    public static <T> T get(T t) {
        if (t instanceof A) {
            return t;
        }
        return null;
    }

    public static void main(String[] args) {
        Object o = A.get(new Object());
        A a = A.get(new A());
        System.out.println(a.a);
    }
}
