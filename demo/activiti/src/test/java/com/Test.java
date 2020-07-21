package com;

import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    //申请流程图
    @Autowired
    RepositoryService repositoryService;

    //开始流程图
    @Autowired
    RuntimeService runtimeService;

    //任务
    @Autowired
    TaskService taskService;

    //用户与用户组
    @Autowired
    IdentityService identityService;

    //历史记录
    @Autowired
    HistoryService historyService;

    @Autowired
    ProcessEngine processEngine;

    @org.junit.jupiter.api.Test
    void a() {
        Deployment deploy = repositoryService.createDeployment().name("申请流程开始").addClasspathResource("processes/test1.bpmn").deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
        System.out.println(deploy.getKey());
        System.out.println(deploy.getTenantId());
    }

    @org.junit.jupiter.api.Test
    void b() {
        Map map = new HashMap();
        map.put("liyou", "我想进");
        map.put("buchong", "去去去");
        ProcessInstance test_key = runtimeService.startProcessInstanceByKey("test_key", map);
        System.out.println(test_key.getProcessDefinitionId());
        System.out.println(test_key.getProcessInstanceId());
        //test_key:1:40004
        //52501 test_key:2:50004
        //42501 57501  test_key:3:57504
        //60001
    }
    @org.junit.jupiter.api.Test
    void task() {
//        List<Task> admin = taskService.createTaskQuery().taskAssignee("admin").list();
//        for (Task task : admin) {
//            System.out.println(task.getExecutionId());
//            System.out.println(task.getProcessDefinitionId());
//            System.out.println(task.getId());
//            System.out.println(task.getProcessInstanceId());
//        }
//        Task task = admin.get(0);
//        Map execution = new HashMap();
//        execution.put("condition", 1);
//        taskService.complete("60009",execution);
//        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId("42501").singleResult();
//        System.out.println(historicProcessInstance.getProcessDefinitionId());
//        System.out.println(historicProcessInstance.getProcessVariables());
        List<Task> manage = taskService.createTaskQuery().taskCandidateUser("dashan2").list();
        if (manage.size() > 0) {
            for (Task task : manage) {
                System.out.println(task.getName());
                System.out.println(task.getId());
            }
        } else {
            System.out.println("没有");
        }

    }

    @org.junit.jupiter.api.Test
    void complete() {
        taskService.complete("65002");
    }


    @org.junit.jupiter.api.Test
    void initUserAndGroup() {
        User user = identityService.newUser("dashan");
        user.setFirstName("da");
        user.setLastName("shan");
        user.setEmail("27555@qq.com");
        user.setPassword("111111");

        User user1 = identityService.newUser("dashan1");
        user1.setFirstName("da");
        user1.setLastName("shan1");
        user1.setEmail("27555@qq.com");
        user1.setPassword("111111");


        User user2 = identityService.newUser("dashan2");
        user2.setFirstName("da");
        user2.setLastName("shan2");
        user2.setEmail("27555@qq.com");
        user2.setPassword("111111");

        User user3 = identityService.newUser("dashan3");
        user3.setFirstName("da");
        user3.setLastName("shan3");
        user3.setEmail("27555@qq.com");
        user3.setPassword("111111");

        identityService.saveUser(user);
        identityService.saveUser(user1);
        identityService.saveUser(user2);
        identityService.saveUser(user3);

        Group group = identityService.newGroup("group");
        group.setName("经理组");
        group.setType("经理组");

        Group group1 = identityService.newGroup("group1");
        group1.setName("总监组");
        group1.setType("总监组");

        Group group2 = identityService.newGroup("group2");
        group2.setName("人力资源组");
        group2.setType("人力资源组");

        identityService.saveGroup(group);
        identityService.saveGroup(group1);
        identityService.saveGroup(group2);



    }
    @org.junit.jupiter.api.Test
    void query() {
        List<User> list = identityService.createUserQuery().orderByUserId().asc().list();
        for (User user : list) {
            System.out.println(user.getFirstName()+" : "+user.getLastName());
        }
        List<Group> list1 = identityService.createGroupQuery().orderByGroupId().asc().list();
        for (Group group : list1) {
            System.out.println(group.getName());
        }
        List<User> group = identityService.createUserQuery().memberOfGroup("group").list();
        for (User user : group) {
            System.out.println(user.getFirstName()+user.getLastName());
        }
        List<Group> dashan = identityService.createGroupQuery().groupMember("dashan").list();
        for (Group group1 : dashan) {
            System.out.println(group1.getName());
        }

    }
    @org.junit.jupiter.api.Test
    void memberShip() {
        identityService.createMembership("dashan","group");
        identityService.createMembership("dashan1", "group1");
        identityService.createMembership("dashan2", "group2");
    }


}