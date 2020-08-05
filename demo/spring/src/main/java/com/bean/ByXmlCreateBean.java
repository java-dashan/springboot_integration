package com.bean;

import com.entity.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

public class ByXmlCreateBean {
    public static void main(String[] args) {
        DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
        Resource resource = new ClassPathResource("bean.xml");
//        Resource properties = new ClassPathResource("application.properties");
//        EncodedResource encodedResource = new EncodedResource(properties, "utf-8");
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(defaultListableBeanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions(resource);
//        xmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);

        BeanFactory parentBeanFactory = defaultListableBeanFactory.getParentBeanFactory();
        System.out.println(parentBeanFactory);

        User bean = defaultListableBeanFactory.getBean(User.class);
        System.out.println(bean);
    }
}
