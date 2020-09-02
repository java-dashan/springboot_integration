package com.activiti6;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.NativeProcessDefinitionQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


/**
 * @auther ADun
 * @date 2020/7/23 14:50
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class StartProcessTest {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void startProcess(){
        String processDefinitionId="MyProcess:1:7";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        //从act_ru_exection中查询
        System.out.println("流程启动成功：\n"+"执行实例id："+processInstance.getId()
                +"\n流程定义id："+processInstance.getProcessDefinitionId()
                +"\n流程实例id："+processInstance.getProcessInstanceId());

    }

    @Test
    public void queryProcessInstanceId() {
        NativeProcessDefinitionQuery nativeProcessDefinitionQuery = repositoryService.createNativeProcessDefinitionQuery();
        List<ProcessDefinition> list = nativeProcessDefinitionQuery.list();
        System.out.println(list);
    }

    @Test
    public void queryProcessInstanceId1() {
        ProcessDefinitionQuery active = repositoryService.createProcessDefinitionQuery().active();
        List<ProcessDefinition> list = active.list();
        System.out.println(list);
//        repositoryService.createModelQuery().
    }

}
