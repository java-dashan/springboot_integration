package com.guice.set;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.guice.Api;

public class TestSet {

    private SetApi setApi;

    private Api api;

    @Inject
    public void setSetApi(SetApi setApi) {
        this.setApi = setApi;
    }

    public void say() {
        setApi.say();
    }
    public void apiSay() {
        api.say();
    }

    @Inject
    public TestSet(Api api) {
        this.api = api;
    }

    public TestSet() {
    }

//    public static void main(String[] args) {
////        set方法注入
//        SetModule setModule = new SetModule();
//        Injector injector= Guice.createInjector(setModule);
//        TestSet testSet = new TestSet();
//        injector.injectMembers(testSet);
//        testSet.say();
////        构造器注入
//        TestSet instance = Guice.createInjector().getInstance(TestSet.class);
//        instance.apiSay();
//    }
}
