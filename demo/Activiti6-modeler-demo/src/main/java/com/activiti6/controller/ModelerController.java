package com.activiti6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 流程控制器
 * @author 贺峥
 */
@Controller
public class ModelerController{

    private static final Logger logger = LoggerFactory.getLogger(ModelerController.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;

    
	@RequestMapping("/index")
	public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        modelAndView.addObject("modelList", repositoryService.createModelQuery().list());
        return modelAndView;
	}

	/**
	 * 列出所有流程模板
	 * @return
	 */
	@RequestMapping("/list_process")
	@ResponseBody
	public List<Model> list() {
		return repositoryService.createModelQuery().list();
	}

	/**
	 * 列出所有流程实例
	 * @return
	 */
	@RequestMapping("/list_process_instance")
	@ResponseBody
	public ModelAndView listInstance(ModelAndView modelAndView) {
//	    List<ProcessDefinitionEntityImpl>
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> list = processDefinitionQuery.list();
        List<ProcessDefinitionEntityImpl> entities = new ArrayList<>();
        list.forEach(pro -> entities.add((ProcessDefinitionEntityImpl)pro));
        modelAndView.setViewName("instance");
        modelAndView.addObject("instances",entities);
        return modelAndView;
	}

    /**
     * 跳转编辑器页面
     * @return
     */
    @GetMapping("/editor")
    public String editor(){
        return "modeler";
    }
    
    
    /**
     * 创建模型
     * @param response
     * @param name 模型名称
     * @param key 模型key
     */
    @RequestMapping("/create")
    public void create(HttpServletResponse response,String name,String key) throws IOException {
    	logger.info("创建模型入参name：{},key:{}",name,key);
        Model model = repositoryService.newModel();
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "");
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
        //存入表act_re_model
        repositoryService.saveModel(model);
        createObjectNode(model.getId());
        response.sendRedirect("/editor?modelId="+ model.getId());
        logger.info("创建模型结束，返回模型ID：{}",model.getId());
    }
    
    /**
     * 创建模型时完善ModelEditorSource，这里是对画布的相关设置
     * @param modelId
     */
	@SuppressWarnings("deprecation")
	private void createObjectNode(String modelId){
    	 logger.info("创建模型完善ModelEditorSource入参模型ID：{}",modelId);
    	 ObjectNode editorNode = objectMapper.createObjectNode();
         editorNode.put("id", "canvas");
         editorNode.put("resourceId", "canvas");
         ObjectNode stencilSetNode = objectMapper.createObjectNode();
         stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
         editorNode.put("stencilset", stencilSetNode);
         try {
			repositoryService.addModelEditorSource(modelId,editorNode.toString().getBytes("utf-8"));
		} catch (Exception e) {
			 logger.info("创建模型时完善ModelEditorSource服务异常：{}",e);
		}
        logger.info("创建模型完善ModelEditorSource结束");
    }
    
    /**
     * 发布流程///流程部署
     * @param modelId 模型ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/publish")
    public Object publish(String modelId){
    	logger.info("流程部署入参modelId：{}",modelId);
    	Map<String, String> map = new HashMap<String, String>();
		try {
			Model modelData = repositoryService.getModel(modelId);//获取相应的模型信息，act_re_model
	        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());//获取相应的流程文件信息，act_ge_bytearray
	        if (bytes == null) {//没有相应的流程文件
	        	logger.info("部署ID:{}的模型数据为空，请先设计流程并成功保存，再进行发布",modelId);
	        	map.put("code", "FAILURE");
	            return map;
	        }
			JsonNode modelNode = new ObjectMapper().readTree(bytes);//解析相应的流程文件
			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
			//部署
	        Deployment deployment = repositoryService.createDeployment()
	        		.name(modelData.getName())//模型名称
	        		.addBpmnModel(modelData.getKey()+".bpmn20.xml", model)
	        		.deploy();//部署相应的流程
	        modelData.setDeploymentId(deployment.getId());//获取流程部署后的流程id
	        repositoryService.saveModel(modelData);//保存到act_re_model表中
	        map.put("code", "SUCCESS");
		} catch (Exception e) {
			logger.info("部署modelId:{}模型服务异常：{}",modelId,e);
			map.put("code", "FAILURE");
		}
		logger.info("流程部署出参map：{}",map);
        return map;
    }
    
    /**
     * 撤销流程定义
     * @param modelId 模型ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/revokePublish")
    public Object revokePublish(String modelId){
    	logger.info("撤销发布流程入参modelId：{}",modelId);
    	Map<String, String> map = new HashMap<String, String>();
		Model modelData = repositoryService.getModel(modelId);//获取相应的模型信息，act_re_model
		if(null != modelData){
			try {
				/**
				 * 参数不加true:为普通删除，如果当前规则下有正在执行的流程，则抛异常 
				 * 参数加true:为级联删除,会删除和当前规则相关的所有信息，包括历史 
				 */
				//根据流程的部署ID删除act_re_procdef、act_re_deployment表中的数据，后面的true是将还有未完成任务的流程强制删除
				repositoryService.deleteDeployment(modelData.getDeploymentId(),true);
				map.put("code", "SUCCESS");
			} catch (Exception e) {
				logger.error("撤销已部署流程服务异常：{}",e);
				map.put("code", "FAILURE");
			}
		}
		logger.info("撤销发布流程出参map：{}",map);
        return map;
    }
    
    /**
     * 删除流程实例  流程正在运行中
     * @param modelId 模型ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteProcessInstance(String modelId){
    	logger.info("删除流程实例入参modelId：{}",modelId);
    	Map<String, String> map = new HashMap<String, String>();
		Model modelData = repositoryService.getModel(modelId);//获取相应的模型信息，act_re_model
		if(null != modelData){
			try {
			    //流程启动后会在act_ru_execution表中查到，根据流程定义的key进行查询
			   ProcessInstance pi = runtimeService.createProcessInstanceQuery().processDefinitionKey(modelData.getKey()).singleResult();
			   if(null != pi) {
                    //根据流程实例ID进行删除，act_ru_execution
				   runtimeService.deleteProcessInstance(pi.getId(), "");
				   //删除历史流程实例act_hi_procinst
				   historyService.deleteHistoricProcessInstance(pi.getId());
			   }
				map.put("code", "SUCCESS");
			} catch (Exception e) {
				logger.error("删除流程实例服务异常：{}",e);
				map.put("code", "FAILURE");
			}
		}
		logger.info("删除流程实例出参map：{}",map);
        return map;
    }

	/**
	 * 启动流程
	 */
	@RequestMapping("/start/{id}")
	public void startProcess(@PathVariable("id")String processDefinitionId){
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
		//从act_ru_exection中查询
		System.out.println("流程启动成功：\n"+"执行实例id："+processInstance.getId()
				+"\n流程定义id："+processInstance.getProcessDefinitionId()
				+"\n流程实例id："+processInstance.getProcessInstanceId());
	}

	@GetMapping("/start")
	@ResponseBody
	public void startProcess1(String processDefinitionId){
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
		//从act_ru_exection中查询
		System.out.println("流程启动成功：\n"+"执行实例id："+processInstance.getId()
				+"\n流程定义id："+processInstance.getProcessDefinitionId()
				+"\n流程实例id："+processInstance.getProcessInstanceId());
	}
}
