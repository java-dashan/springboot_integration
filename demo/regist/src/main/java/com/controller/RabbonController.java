package com.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rabbon")
public class RabbonController {

    @Autowired
    EurekaClient eurekaClient;
//    @Autowired
//    DiscoveryClient discoveryClient;
    @LoadBalanced
    InstanceInfo instanceInfo;

    @GetMapping("/t1")
    public void test1() {
        Applications applications = eurekaClient.getApplications();
        List<Application> registeredApplications = applications.getRegisteredApplications();
        System.out.println(registeredApplications);
    }
    @GetMapping("/t2")
    public void test2() {
//        List<InstanceInfo> instancesById = discoveryClient.getInstancesById("");
        System.out.println(instanceInfo);
    }
}
