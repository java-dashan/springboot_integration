package com.guice.set;

import com.google.inject.Singleton;


@Singleton // 保证单例
public class SetApiImpl implements SetApi {
    @Override
    public void say() {
        System.out.println("set api impl");
    }
}
