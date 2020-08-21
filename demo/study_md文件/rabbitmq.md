## 		springboot+rabbitmq,spring-boot-starter-amqp使用

#### 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```



#### 配置

```yaml
spring:
    rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
```



### 1.声明直连交换机

```java
@Component
@RabbitListener(bindings = @QueueBinding(value = @Queue("myDirectQueue"),
				exchange = @Exchange(value = "myDirectExchange",type = ExchangeTypes.DIRECT),key = "mine.direct"                                        
))

public class MyDirectListener {    
    @RabbitHandler
    public void onMessage(@Payload String msg) {
        System.out.println(msg);
    }
    
}
```

### 2.生产者

```java
@Autowirde
RabbitTemplate rabbitTemplate;

private void send(){    
    rabbitTemplate.convertAndSend("myDirectExchange","mine.direct","this is a message");
}


```

### 1.Default 默认交换机































