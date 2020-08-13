## 									activiti学习

### 1.基本架构图



![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\activiti架构图.jpg)



### 2.七大service接口

**RepositoryService**

Activiti 中每一个不同版本的业务流程的定义都需要使用一些定义文件，部署文件和支持数据 ( 例如 BPMN2.0 XML 文件，表单定义文件，流程定义图像文件等 )，这些文件都存储在 Activiti 内建的 Repository 中。Repository Service 提供了对 repository 的存取服务,流程仓库service，用于管理流程仓库，例如部署，删除，读取流程资源



**RuntimeService**

在Activiti中，每个流程定义被启动一次之后，都会生成相应的流程对象实例。RunTimeService提供启动流程，查询流程实例，设置获取流程实例变量等功能。此外还提供对流程部署，流程定义和流程实例存取的服务。



**TaskService**

在activiti业务流程定义的每一个执行节点被称为一个task，对流程中的数据存取，状态变更等操作都需要在task中完成。TaskService提供了对用户task和form的相关操作。提供了运行时任务的查询、领取、完成、删除以及变量设置等功能。



**IdentityService**

Activiti内置了用户以及用户组的概念以及功能，必须使用用户或者用户组才能获取到相应的task。IdentityService提供了对用户和用户组的管理功能。



**HistoryService**

主要用于获取正在运行或者已经运行结束的流程实例信息，与RunTimeService获取的流程信息不同，历史信息包含已经持久储存化的信息，并已经针对查询做出优化。



**FormService**

Activiti中的流程和状态Task均可关联相关的业务数据，通过FormService可以存取启动和完成任务所需的表单数据并根据需要来渲染表单。



**ManagementService**

ManagementService提供对流程引擎的管理和维护的功能，这些功能不在工作流驱动的应用程序中使用，主要运用activiti的日常维护。


