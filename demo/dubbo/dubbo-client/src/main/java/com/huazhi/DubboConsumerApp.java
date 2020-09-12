package com.huazhi;

import com.huazhi.service.ISayService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class DubboConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApp.class, args);
    }

    @DubboReference(protocol = "dubbo")
    ISayService iSayService;

    @GetMapping("/say")
    public String say() {
        return iSayService.sayHello();
    }
}
