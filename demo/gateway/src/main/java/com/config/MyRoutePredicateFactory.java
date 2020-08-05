package com.config;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.HeaderRoutePredicateFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;


public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

    public MyRoutePredicateFactory() {
        super(HeaderRoutePredicateFactory.Config.class);
    }

//    @Override
//    public Predicate<ServerWebExchange> apply(Config config) {
//        // grab configuration from Config object
//        return exchange -> {
//            //grab the request
//            ServerHttpRequest request = exchange.getRequest();
//            //take information from the request to see if it
//            //matches configuration.
//            return this.apply();
//        };
//    }

    @Override
    public Predicate<ServerWebExchange> apply(HeaderRoutePredicateFactory.Config config) {
        return null;
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

}
