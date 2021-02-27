package com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaApp {
    public static void main(String[] args) {
//        SpringApplication.run(KafkaApp.class, args);
        String a = "ax" + "class";
        String c = new String("ax") + new String("class");
        String b = "axclass";
        System.out.println(a == b);
        System.out.println(b == c);
    }
}
