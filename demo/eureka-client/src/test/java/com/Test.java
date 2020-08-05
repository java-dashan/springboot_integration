package com;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @org.junit.Test
    public void test1() {
//        loadBalancerClient.
    }
}
