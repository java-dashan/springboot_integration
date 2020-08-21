# 							spring cloud

### 1.发展历程

1. 单体应用

   每个项目都会发布一个单体应用，即一个war包部署到tomcat,每次需要增加新的功能都会在原来的基础身上不断迭代,导致war包不断膨胀,维护及其困难，隐患也不断增加。运行期间任何一个bug都会导致系统宕机。

2. 架构演进
   - SOA面向服务架构
   - ESB
   - 架构要求：
     1. 高性能:
     2. 独立性:每个模块出现bug不影响其他模块
     3. 易扩展:每个节点都可以根据实际需要进行扩展
     4. 便于管理:各模块的资源能够轻松管理升级及维护
     5. 状态监控与警报:某个节点出现问题能够被及时发现
   - 事件驱动架构：
   
   ​        1.EventObject
   
   ​        2.EventListener

### 2.服务管理Eureka

基于REST服务的分布式中间件,主要用于服务管理 

1. 服务器端
2. 服务提供者
3. 服务调用者

```json
//服务端
1.pom文件添加依赖   spring-cloud-starter-erueka-server(版本号通过dependencyManagement管理)
2.启动类上添加注解@EnableEurekaServer
3.yam文件配置端口   8671
4.erueka:    //不添加会报错
	client:
		registerWithEureka:false   //是否将自己注册到erueka
		fetchRegistry:false		//是否抓取注册信息
```

```yaml
#服务提供者/服务调用者
1.pom文件添加依赖   spring-cloud-starter-erueka-client(版本号通过dependencyManagement管理)
2.启动类上添加注解@EnableEurekaClient/EnableDiscoveryClient
3.yam如下
spring:
	application:
		name: aaa
eureka:
	instance:
		hostname: localhost
	client:
		serviceUrl:
			defaultZone: http://localhost:8671/eureka/
```





### 3.容错机制Hystrix

通过增加延迟阈值以及容错的逻辑,帮助我们控制分布式系统间组件的交互  

作用：

-  当所依赖的网络服务发生延迟或者失败时,对访问的客户端程序进行保护
- 在分布式系统中,停止级联故障
- 网络服务回复正常后,可以快速回复客户端的访问能力
- 调用失败时执行服务回退
- 可支持实时监控,报警和其他操作

**服务容错：**强调容忍错误,不至于整体故障

```json
1：commandKey：配置全局唯一标识服务的名称，比如，库存系统有一个获取库存服务，那么就可以为这个服务起一个名字来唯一识别该服务，如果不配置，则默认是@HystrixCommand注解修饰的函数的函数名。

2：groupKey：一个比较重要的注解，配置全局唯一标识服务分组的名称，比如，库存系统就是一个服务分组。通过设置分组，Hystrix会根据组来组织和统计命令的告、仪表盘等信息。Hystrix命令默认的线程划分也是根据命令组来实现。默认情况下，Hystrix会让相同组名的命令使用同一个线程池，所以我们需要在创建Hystrix命令时为其指定命令组来实现默认的线程池划分。此外，Hystrix还提供了通过设置threadPoolKey来对线程池进行设置。建议最好设置该参数，使用threadPoolKey来控制线程池组。

3：threadPoolKey：对线程池进行设定，细粒度的配置，相当于对单个服务的线程池信息进行设置，也可多个服务设置同一个threadPoolKey构成线程组。

4：fallbackMethod：@HystrixCommand注解修饰的函数的回调函数，@HystrixCommand修饰的函数必须和这个回调函数定义在同一个类中，因为定义在了同一个类中，所以fackback method可以是public/private均可。

5：commandProperties：配置该命令的一些参数，如executionIsolationStrategy配置执行隔离策略，默认是使用线程隔离，此处我们配置为THREAD，即线程池隔离。参见：com.netflix.hystrix.HystrixCommandProperties中各个参数的定义。

6：threadPoolProperties：线程池相关参数设置，具体可以设置哪些参数请见：com.netflix.hystrix.HystrixThreadPoolProperties
　
7：ignoreExceptions：调用服务时，除了HystrixBadRequestException之外，其他@HystrixCommand修饰的函数抛出的异常均会被Hystrix认为命令执行失败而触发服务降级的处理逻辑（调用fallbackMethod指定的回调函数），所以当需要在命令执行中抛出不触发降级的异常时来使用它，通过这个参数指定，哪些异常抛出时不触发降级（不去调用fallbackMethod），而是将异常向上抛出。
　　
8：observableExecutionMode：定义hystrix observable command的模式；
      
9：raiseHystrixExceptions：任何不可忽略的异常都包含在HystrixRuntimeException中；
     
10：defaultFallback：默认的回调函数，该函数的函数体不能有入参，返回值类型与@HystrixCommand修饰的函数体的返回值一致。如果指定了fallbackMethod，则fallbackMethod优先级更高。

@CacheResult:该注解用来标记请求命令返回的结果应该被缓存，它必须与@HystrixCommand注解结合使用
	//属性值：cacheKeyMethod

@CacheRemove:该注解用来让请求命令的缓存失效，失效的缓存根据定义Key决定.//属性值：commandKey,cacheKeyMethod

@CacheKey:该注解用来在请求命令的参数上标记，使其作为缓存的Key值，如果没有标注则会使用所有参数。如果同事还是使用了@CacheResult和@CacheRemove注解的cacheKeyMethod方法指定缓存Key的生成，那么该注解将不会起作用.//属性值：value
```





**服务降级：**强调服务非强依赖,不影响主要流程

### 4.REST客户端Feign

REST客户端,目的简化WEB service客户端的开发

- 编码器encoder,Bean名称包括feignDecoder,ResponseEntityDecoder
- 解码器decoder,Bean名称包括feignEncoder,SpringEncoder
- 注解翻译器Contract.BaseContract,Bean名称包括feignContract,SpringMvcContract
- 请求拦截器RequestInterceptor
- 接口日志logger,Bean名称包括feignLogger,Slf4jLogger
  - NONE:默认值
  - BASIC:记录请求方法,URL,相应状态代码和执行时间
  - HEADERS:除了BASIC记录的信息之外,嗨包括请求头和响应头
  - FULL:记录全部日志,包括请求头,请求体,请求与响应的元数据
- 压缩配置
  - feign.compression.request.enabled:设置为true开启请求压缩
  - feign.compression.response.enabled:设置为true开启响应压缩
  - feign.compression.request.mime-types:数据类型列表,默认值为text/xml,application/xml,application/json
  - feign.compression.request.min-request-size:设置请求内容的最小阈值,默认值为2048

```java
Feign.builder().encoder().decoder.contract().requestInterceptor().logger(new Logger.JavaLogger().appendToFile("logs/http.log")).target(Person.class,"http://localhost:8080/")
```



### 5.负载均衡Ribbon

在微服务集群中为各个客户端的通信提供支持,它主要实现中间层应用的负载均衡

- 负载均衡器ILoadBalance
- 负载规则IRule
- Ping机制IPing

### 6.网关Zuul

为微服务集群提供代理，过滤，路由等功能

通过RequestContext共享上下文

```java
//请求如果带有参数token则放行
public Object run() {
    RequestContext context = RequestContext.getCurrentContext();
	HttpServletRequest request = context.getRequest();
    Object accessToken = request.getParameter("token");
    if(accessToken == null) {
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
        try {
            context.getResponse().getWriter().write("token is empty");
        } catch (Exception e) {
            return null;
        }
    } 
    return null;
}
```



### 7.配置中心config

为分布式系统提供了配置服务器和配置客户端,通过对它们的配置,可以很好地管理集群中的配置文件。

![1590897802550](C:\Users\da\AppData\Roaming\Typora\typora-user-images\1590897802550.png)

![1590897774362](C:\Users\da\AppData\Roaming\Typora\typora-user-images\1590897774362.png)

- 本地仓库

1.激活配置服务器  `@EnableConfigServer`

```properties
## ${user.dir} 根目录的绝对路径,减少平台文件系统的不一致

spring.cloud.config.server.git.uri = ${user.dir}/src/main/resources/configs 

# config下 三文件 abc.properties abc-test.properties abc-prod.properties
```

2.将properties配置的目录 git 一下(初始化成本地仓库 )

```tex
git init

git add .

git commit -m "first commit"
```

3.测试  (  localhost:8080/{applicationName}/{profile}/{label}     label代表分支  )

访问 localhost:8080/abc/test



- 



### 8.服务跟踪Sleuth

服务跟踪框架,可以与Zipkin，Apache HTrace和ELK等数据分析,服务跟踪系统进行整合,为服务跟踪,解决问题提供了便利。

### 9.消息驱动Stream

用于构建消息驱动微服务的框架，该框架在Spring boot的基础上，整合了spring Integration来连接消息代理中间件

### 10.消息总线Bus

连接RabbitMQ，Kafka等消息代理的集群消息总线。

### 11.系统监控Actuator

提供多个服务端点,供外部应用看到应用程序的健康情况

```properties
management.security.enabled = true
访问localhost:9090/beans
#2.x以后配置如下
#访问http://localhost:1001/actuator/env
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

端点:

- /env
- /health
- /beans

![1590388742443](C:\Users\da\AppData\Roaming\Typora\typora-user-images\1590388742443.png)

### 12.题外

一个Spring Cloud应用程序通过创建一个“引导”上下文来进行操作，这个上下文是主应用程序的父上下文。开箱即用，负责从外部源加载配置属性，还解密本地外部配置文件中的属性。这两个上下文共享一个`Environment`，这是任何Spring应用程序的外部属性的来源。

- Springcloud 配置文件加载器: PropertySourceLocator
- Springboot 配置文件加载器: PropertiesPropertySourceLoader,YamlPropertySourceLoader

#### 禁用引导过程:

```properties
#通过设置，（例如在系统属性中）来完全禁用引导过程
spring.cloud.bootstrap.enabled=false
```

#### `bootstrap`配置文件属性:

```properties
#更改cloud配置文件:
spring.cloud.bootstrap.name = ...
#或者
spring.cloud.bootstrap.location = ...
#如果要允许您的应用程序使用自己的系统属性或配置文件覆盖远程属性，则远程属性源必须通过设置
spring.cloud.config.allowOverride=true
#覆盖任何本地属性源，
spring.cloud.config.overrideNone=true
#如果只有系统属性和env var应该覆盖远程设置，而不是本地配置文件。
spring.cloud.config.overrideSystemProperties=false
#设置记录器级别
logging.level.*
```

#### 环境变化

- EnvironmentChangeEvent
- ApplicationListeners
- @RefreshScope:`RefreshScope`是上下文中的一个bean，它有一个公共方法`refreshAll()`来清除目标缓存中的范围内的所有bean。还有一个`refresh(String)`方法可以按名称刷新单个bean。此功能在`/refresh`端点（通过HTTP或JMX）中公开。

#### 服务自动注册

默认情况下，`ServiceRegistry`实现将自动注册正在运行的服务。要禁用该行为，有两种方法。您可以设置`@EnableDiscoveryClient(autoRegister=false)`永久禁用自动注册。您还可以设置`spring.cloud.service-registry.auto-registration.enabled=false`以通过配置禁用该行为。



### 13.项目实战

- 全局异常处理

```java
//springboot 全局异常处理 ResponseEntity<>.ok().body(map)可以使用，其他例如ResponseEntity<>.status(404).body(map)报错
@RestControllerAdvice
public class GlobelExceptionHandler {

    @ExceptionHandler(SocialExcepiton.class)
    public Map exception(SocialExcepiton e){
        Map map = new HashMap();
        map.put("message", e.getMessage());
        map.put("status",e.getStatus());
        System.out.println(111);
        //ResponseEntity.ok().body(map); 返回下面这个报错
        System.out.println(new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST).toString());
        return map;
    }
}    
```

- spring事件/监听机制

```java
public abstract class ApplicationEvent extends EventObject{}
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener{}

public class test{
    
    public static void main(String args[]){
        AnnotationApplicationContext context = new AnnotationApplicationContext();
        //增加监听器 一
        context.addApplicationListener(new MyApplicationListener());
        //增加监听器 二 注册congfiguration的方式
        //在MyApplicationListener 加@Component
        //context.regiest(MyApplicationListener.class);
        
        //启动上下文
        context.refresh();
        //发布事件
        context.publishEvent(new MyApplicationEvent("hello"));
    }
    
}

public class MyApplicationEvent extends ApplicationEvent{
    public MyApplicationEvent(Object source){
        super(source);
    }
}

public class MyApplicationListener extends ApplicationListener<MyApplicationEvent>{
    @Override
    public void onApplicationEvent(MyApplicationEvent e){
        System.out.println(e.getSource());
    }
}

```

- springboot上下文/springcloud上下文

```java
//ConfigurableApplicationContext  根接口
//AnnotationConfigApplicationContext 注解配置上下文(现在最常用)
1.springboot上下文
    非web应用:AnnotationApplicationContext classpathApplicationContext

    web应用：AnnotationConfigEmbeddedWebApplicationContext    
2.springcloud上下文：Bootstrap(父)        
```

- springboot 应用运行监听器--SpringApplicationRunListener

```java
//事件触发类 EventPublishingRunListener SpringApplication.run()中触发 如this.starting()

ApplicationStartedEvent
    
ApplicationEnvironmentPreparedEvent
    
ApplicationPreparedEvent
    
ApplicationReadyEvent/ApplicationFailedEvent    
```

- Spring Environmennt	

  Enviroment 是一种在容器内以配置(profile) 和属性(properties)为模型的应用环境抽象整合。
  Spring Framework 提供了两种Environment的实现
      一般应用：`StandardEnvironment`
      web应用：`StandardServletEnvironment`

  Profile

  应用程序可通过调用`ConfigurableEnvironment`接口控制Profile激活

  - setActiveProfiles(String ...)
  - addActiveProfile(String )
  - setDefaultProfiles(String ...)

  properties

  PropertySource  单一属性   n:1 PropertySources 

  PropertySources 组合属性 1:1 Environmennt	
  
  加载器：
  
  `PropertySourceLoader`
  
  - PropertiesPropertySourceLoader
  - YamlPropertySourceLoader
  
  运行优先级：
  
  jvm 参数 > program参数 > bootstrap.properties > application.properties 
  
  覆盖：
  
   application.properties 会覆盖  bootstrap.properties 相同内容

```java
//请求端点 /env
//数据来源 EnvironmentEndpoint
//Controller来源 EnvironmentMvcEndPoint
```

- `ConfigFileApplicationListener`

  用于读取默认profile 关联的配置文件( application.properties / yaml)









- 自定义属性配置源

  - 方式1  

  ```properties
  #加载插件 META-INF/spring.factories  注意该类要实现ApplicationContextInitializer
  org.springframework.cloud.bootstrap.BootstrapConfiguration = \
  com.xxx.Myconfiguration
  ```

  ```java
  public class Myconfiguration implements ApplicationContextInitializer{
      @Override
      public void initialize(ConfigurableApplicationContext applicationContext){
          //获取环境
          ConfigurableEnvironment env = applicationContext.getEnvironment();
          //获取PropertySources
          MutablePropertySources propertySources = env.getPropertySources();
          //添加自定义配置源
          propertySources.addFirst(createPropertySources());
      }
      
      private PropertySource createPropertySources(){
          Map <String,Object> source = new HashMap<>();
          
          map.put("name","dashan");
          
          PropertySource pro = new MapPropertySource("new_properties" ,source);
          
          return pro;
      }
  }
  ```

  - 方式二

  ```properties
  #加载插件 spring.factories  注意该类要实现ApplicationContextInitializer
  org.springframework.cloud.bootstrap.BootstrapConfiguration = \
  com.xxx.MyPropertySourceLocator
  ```

  ```java
  @Configuration
  public class MyPropertySourceLocator implements PropertySouceLocator{
      @Override 
      public PropertySource<?> locate(Environment environment){
          if(environment instanceof ConfigurableEnvironment){
              ConfigurableEnvironment env = ConfigurableEnvironment.class.cast(environment);
              //获取PropertySources
          MutablePropertySources propertySources = env.getPropertySources();
          //添加自定义配置源
          propertySources.addFirst(createPropertySources());
          }
          return null;
      }
      
      private PropertySource createPropertySources(){
          Map <String,Object> source = new HashMap<>();
          
          map.put("name","dashan");
          
          PropertySource pro = new MapPropertySource("new1_properties" ,source);
          
          return pro;
      }
  }
  ```

  

- 日志
  
  - LoggiingApplicationListener

```java
// application.properties 配置 logging.config = xxx.xml
//最好使用默认的(resources/spring-logback.xml),不要配置
```







