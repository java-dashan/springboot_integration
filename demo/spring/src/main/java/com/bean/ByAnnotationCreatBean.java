package com.bean;

import com.aop.MyAspect;
import com.entity.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class ByAnnotationCreatBean {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ByAnnotationCreatBean.class);
        context.register(MyAspect.class);
        context.refresh();
        User user = context.getBean("user1", User.class);
        user.setName("a");
        System.out.println(user);
        System.out.println(user.getName());
    }
    @Bean
    User user1() {
        User user = new User();
        user.setId("1");
        user.setName("da");
        return user;
    }

    @Bean
    User user2() {
        User user = new User();
        user.setId("2");
        user.setName("da2");
        return user;
    }
}
