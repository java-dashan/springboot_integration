RepositoryService	仓库服务，用于管理仓库，比如部署或删除流程定义、读取流程资源等。
IdentifyService	身份服务，管理用户、组以及它们之间的关系。
RuntimeService	运行时服务，管理所有正在运行的流程实例、任务等对象。
TaskService	任务服务，管理任务。
FormService	表单服务，管理和流程、任务相关的表单。
HistroyService	历史服务，管理历史数据。
ManagementService	引擎管理服务，比如管理引擎的配置、数据库和作业等核心对象
表	说明
ACT_RE_*	RE’表示repository。 这个前缀的表包含了流程定义和流程静态资源 （图片，规则，等等）
ACT_RU_*	RU’表示runtime。这些运行时的表，包含流程实例，任务，变量，异步任务，等运行中的数据。 Activiti只在流程实例执行过程中保存这些数据，在流程结束时就会删除这些记录。 这样运行时表可以一直很小速度很快。
ACT_ID_*	‘ID’表示identity。 这些表包含身份信息，比如用户，组等等。
ACT_HI_*	‘HI’表示history。 这些表包含历史数据，比如历史流程实例， 变量，任务等等。
ACT_GE_*	通用数据， 用于不同场景下，如存放资源文件。

TaskListener

public class MyListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("dashan");
    }
}

@SpringBootApplication(exclude = {LiquibaseAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})

bpmn图可以使用 Activiti-app / Activiti modeler 开源程序进行绘制
