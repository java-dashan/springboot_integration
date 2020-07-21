package com.service;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gourd
 * @description 工作流服务
 * @date 2018/10/30 11:25
 **/
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
//@DS(value = "activiti")
public class WorkFlowServiceImpl{
    @Autowired
    private RepositoryService repositoryService;
 
    @Autowired
    private RuntimeService runtimeService;
 
    @Autowired
    private TaskService taskService;
 
    @Autowired
    private HistoryService historyService;
 
    @Autowired
    private ProcessEngine processEngine;
 
    public static final String DEAL_USER_ID_KEY = "dealUserId";
 
    public static final String DELEGATE_STATE = "PENDING";
 
    /**
     * 启动工作流
     *
     * @param pdKey
     * @param businessKey
     * @param variables
     * @return
     */
    
    public String startWorkflow(String pdKey, String businessKey, Map<String,Object> variables) {
        ProcessDefinition processDef = getLatestProcDef(pdKey);
        if (processDef == null) {
            // 部署流程
            processEngine.getRepositoryService()
                    .createDeployment()//创建部署对象
                    .name(pdKey)
                    .addClasspathResource("processes/"+pdKey+".bpmn")
                    .deploy();
            processDef = getLatestProcDef(pdKey);
        }
        ProcessInstance process  = runtimeService.startProcessInstanceById(processDef.getId(), businessKey, variables);
        return process.getId();
    }
 
    /**
     * 继续流程
     *
     * @param taskId
     * @param variables
     */
    
    public void continueWorkflow(String taskId, Map variables){
        //根据taskId提取任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        DelegationState delegationState = task.getDelegationState();
        if(delegationState != null && DELEGATE_STATE.equals(delegationState.toString())){
            // 委托任务，先需要被委派人处理完成任务
            taskService.resolveTask(taskId,variables);
        }else {
            // 当前受理人
            String dealUserId =variables.get(DEAL_USER_ID_KEY).toString();
            // 签收
            taskService.claim(taskId, dealUserId);
        }
        // 设置参数
        taskService.setVariables(taskId, variables);
        // 完成
        taskService.complete(taskId);
    }
 
    /**
     * 委托流程
     * @param taskId
     * @param variables
     */
    
    public void delegateWorkflow(String taskId, Map variables){
        // 受委托人
        String dealUserId =variables.get(DEAL_USER_ID_KEY).toString();
        // 委托
        taskService.delegateTask(taskId, dealUserId);
    }
 
    /**
     * 结束流程
     * @param pProcessInstanceId
     */
    
    public void endWorkflow(String pProcessInstanceId,String deleteReason){
        // 结束流程
        runtimeService.deleteProcessInstance(pProcessInstanceId, deleteReason);
    }
 
    /**
     * 获取当前的任务节点
     * @param pProcessInstanceId
     */
    
    public String getCurrentTask(String pProcessInstanceId){
        Task task = taskService.createTaskQuery().processInstanceId(pProcessInstanceId).active().singleResult();
        return task.getId();
    }
 
    /**
     *
     * 根据用户id查询待办流程实例ID集合
     *
     */
    
    public List<String> findUserProcessIds(String userId, String pdKey, Integer pageNo, Integer pageSize) {
        List<Task> resultTask;
        if(pageSize == 0 ){
            // 不分页
            resultTask = taskService.createTaskQuery().processDefinitionKey(pdKey)
                    .taskCandidateOrAssigned(userId).list();
        }else {
            resultTask = taskService.createTaskQuery().processDefinitionKey(pdKey)
                    .taskCandidateOrAssigned(userId).listPage(pageNo-1,pageSize);
        }
        //根据流程实例ID集合
        List<String> processInstanceIds = resultTask.stream()
                .map(task -> task.getProcessInstanceId())
                .collect(Collectors.toList());
        return processInstanceIds == null ? new ArrayList<>() : processInstanceIds;
    }
 
    /**
     * 获取流程图像，已执行节点和流程线高亮显示
     */
    
    public void getProcessImage(String pProcessInstanceId, HttpServletResponse response) {
        log.info("[开始]-获取流程图图像");
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        InputStream imageStream = null;
        try (OutputStream os = response.getOutputStream()){
            //  获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(pProcessInstanceId).singleResult();
 
            if (historicProcessInstance == null) {
                throw new ServiceException("获取流程实例ID[" + pProcessInstanceId + "]对应的历史流程实例失败！");
            } else {
                // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(pProcessInstanceId).orderByHistoricActivityInstanceId().asc().list();
 
                // 已执行的节点ID集合
                List<String> executedActivityIdList = new ArrayList<String>();
                int index = 1;
                log.info("获取已经执行的节点ID");
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    executedActivityIdList.add(activityInstance.getActivityId());
 
                    log.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                    index++;
                }
                // 获取流程定义
                BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
 
                // 已执行的线集合
                List<String> flowIds = getHighLightedFlows(bpmnModel, historicActivityInstanceList);
 
                // 流程图生成器
                ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
                // 获取流程图图像字符流（png/jpg）
                imageStream = pec.generateDiagram(bpmnModel, "jpg", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
 
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
 
            }
            log.info("[完成]-获取流程图图像");
        } catch (Exception e) {
            log.error("【异常】-获取流程图失败！",e);
        }finally {
            if(imageStream != null){
                try {
                    imageStream.close();
                } catch (IOException e) {
                    log.error("关闭流异常：",e);
                }
            }
        }
    }
 
    public List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 24小时制
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<String>();
 
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 对历史流程节点进行遍历
            // 得到节点定义的详细信息
            FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());
 
            // 用以保存后续开始时间相同的节点
            List<FlowNode> sameStartTimeNodes = new ArrayList<FlowNode>();
            FlowNode sameActivityImpl1 = null;
            // 第一个节点
            HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);
            HistoricActivityInstance activityImp2_;
 
            for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
                // 后续第1个节点
                activityImp2_ = historicActivityInstances.get(k);
 
                if (activityImpl_.getActivityType().equals("userTask") && activityImp2_.getActivityType().equals("userTask") &&
                        df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))) {
                    // 都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
                } else {
                    //找到紧跟在后面的一个节点
                    sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());
                    break;
                }
 
            }
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                // 后续第一个节点
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
                // 后续第二个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);
 
                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            // 取出节点的所有出去的线
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows();
            // 对所有的线进行遍历
            for (SequenceFlow pvmTransition : pvmTransitions) {
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(pvmTransition.getTargetRef());
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
 
        }
        return highFlows;
 
    }
 
    /**
     * 获取最新版本流程
     *
     * @param modelName
     * @return
     */
    private ProcessDefinition getLatestProcDef(String modelName) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelName).
                latestVersion().singleResult();
    }

    private class ServiceException extends RuntimeException {
        public ServiceException(String s) {

        }
    }
}