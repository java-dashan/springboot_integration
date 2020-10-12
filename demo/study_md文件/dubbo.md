## 							Dubbo

### 1.dubbo入门使用

- 一般建立三个项目：

-  接口项目，服务消费方)

```java
public class User{
    private Integer id;
    private String username;
    private String password;
}

public interface UserService{
    String sayHi(String name);
    User getUser(Integer id);
}
//将其打包
```

- 服务提供方

```yaml
# dubbo扫描路径
dubbo.scan.base-packages=com.huazhi.service

dubbo.protocol.id=dubbo
dubbo.protocol.name=dubbo
dubbo.application.name=dubbo-server

dubbo.protocol.port=-1
dubbo.registry.address=nacos://192.168.139.131:8848
```

```pom
<dependencies>
        <dependency>
            <groupId>com.huazhi</groupId>
            <artifactId>dubbo-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>
</dependencies>
```

```java
@EnableDiscoveryClient
@SpringBootApplication
public class DubboServerApp {
    public static void main(String[] args) {

        SpringApplication.run(DubboServerApp.class, args);
    }
}

@DubboService(protocol = "dubbo")
public class ISayServiceImpl implements ISayService {
    @Override
    public String sayHello() {
        return "第一个接口";
    }
}
```

- 服务消费方

```pom
<dependencies>
        <dependency>
            <groupId>com.huazhi</groupId>
            <artifactId>dubbo-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.8</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>1.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
</dependencies>
```

```yaml
dubbo.application.name=dubbo-consumer
spring.application.name=dubbo-client
dubbo.registry.address=nacos://192.168.139.131:8848
```

```java
@RestController
@SpringBootApplication
public class DubboConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApp.class, args);
    }

    @DubboReference(protocol = "dubbo")
    ISayService iSayService;

    @GetMapping("/say")
    public String say() {
        return iSayService.sayHello();
    }
}
```

