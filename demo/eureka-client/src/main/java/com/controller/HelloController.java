package com.controller;


import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        Applications applications = eurekaClient.getApplications();
        System.out.println(applications);
        List instancesById = eurekaClient.getInstancesById("eureka-client");
        List instancesById1 = eurekaClient.getInstancesById("eureka-client8001");

        return "hello";
    }

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    LoadBalancerClient balancerClient;

//    @Autowired  //错误
//    com.netflix.discovery.DiscoveryClient discoveryClient1;

    @Autowired
    EurekaClient eurekaClient;

//    @Autowired


    @GetMapping("/discover")
    public String discover() {
        List<String> services = discoveryClient.getServices(); //服务名称
        services.forEach(s ->System.out.println(s));
        List<ServiceInstance> instances = discoveryClient.getInstances("eureka-client");//服务实例
        instances.forEach(instance -> {
//            System.out.println(instance.getHost());
//            System.out.println(instance.getMetadata());
//            System.out.println(instance.getUri());
//            System.out.println(instance.getInstanceId());
            System.out.println(instance);
        });

//        baseLoadBalancer.

        return "ok";
    }
}
