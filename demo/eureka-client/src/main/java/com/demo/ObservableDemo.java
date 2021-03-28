package com.demo;


import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscriber;


@Slf4j
public class ObservableDemo {

    public static void main(String[] args) {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world");
                subscriber.onNext("i am java development");
                subscriber.onCompleted();
                throw new RuntimeException("aaa");
            }
        });


        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                log.info("subscriber is on completed");
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("subscriber is on error");
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onNext(String s) {
                log.info("subscriber is on next");
                log.info(s);
            }
        };

        observable.subscribe(subscriber);
    }

}
