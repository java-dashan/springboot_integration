package com.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public class PostGateWayFilterFactory extends AbstractGatewayFilterFactory<PostGateWayFilterFactory.Config> {
    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) ->
                chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    //Manipulate the response in some way
                }));

    }

    public static class Config {
    }
}
