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

```java
//引入 dubbo,接口项目,starter-web,zookerper,mysql,mybatis依赖
//开启dubbo @EnableDubboConfiguration
//配置dubbo spring 依赖
@Component
@Service(version = "1.0.0", timeout = 10000) //dubbo注解
public class UserServiceImpl implement UserService{
    @Autowired
    private UserMapper userMapper;
    
    public String sayHi(String name){
        return "hello,"+ name;
    }
    public User getUser(Integer id){
        userMapper.selectByPrimaryKey(id);
    }
}
```

- 服务消费方

```java
//引入 dubbo,接口项目,starter-web,zookerper依赖
//配置dubbo spring 依赖
//开启dubbo @EnableDubboConfiguration
@RestController
public class UserController{
    @Reference(version="1.0.0")
    private UserService userService;
    
    @GetMapping("/user")
    public User getUser(Integer id){
        return userService.getUser(id);
    }
}

```

