package com.huazhi;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 */

@EnableDiscoveryClient
@SpringBootApplication
public class DubboServerApp {
    public static void main(String[] args) {

        SpringApplication.run(DubboServerApp.class, args);
    }
}
