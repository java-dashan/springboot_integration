package com.guice;

import com.google.inject.Singleton;

@Singleton
public class ApiImpl implements Api {
    @Override
    public void say() {
        System.out.println("aaa");
    }
}
