package com;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.converters.Auto;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
    @Autowired
    EurekaClient eurekaClient;
    @Autowired
    DiscoveryClient discoveryClient;
    @LoadBalanced
    InstanceInfo instanceInfo;

    @org.junit.Test
    public void test1() {
        Applications applications = eurekaClient.getApplications();
        List<Application> registeredApplications = applications.getRegisteredApplications();
        System.out.println(registeredApplications);
    }

    @org.junit.Test
    public void test2() {
        List<InstanceInfo> instancesById = discoveryClient.getInstancesById("");

    }
}
