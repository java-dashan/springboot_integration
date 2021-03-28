package com.test.java;

class ThreadLocalVariable {
    public static void say() {
        try {
            System.out.println(ThreadLocalDemo.threadLocal.get());
        } finally {
            ThreadLocalDemo.threadLocal.remove();
        }
    }
}