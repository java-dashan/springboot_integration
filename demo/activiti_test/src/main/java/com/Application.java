package com;


import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    CommandLineRunner init(final RepositoryService repositoryService,
                           final RuntimeService runtimeService,
                           final TaskService taskService) {

        return new CommandLineRunner() {

            public void run(String... strings) throws Exception {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("applicantName", "John Doe");
                variables.put("email", "john.doe@activiti.com");
                variables.put("phoneNumber", "123456789");
                runtimeService.startProcessInstanceByKey("hireProcess", variables);
            }
        };
    }
}
