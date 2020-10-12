package com.huazhi.service;


import org.apache.dubbo.config.annotation.DubboService;

@DubboService(protocol = "dubbo")
public class ISayServiceImpl implements ISayService {
    @Override
    public String sayHello() {
        return "第一个接口";
    }
}
