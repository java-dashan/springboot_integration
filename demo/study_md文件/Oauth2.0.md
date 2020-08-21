# 			Spring Security + Oauth2.0

问：

  1. Oauth2.0是如何嵌入security?

  2. Oauth2.0是如何协同security进行工作？

     #### ==重要配置类==

     - AuthorizationServerSecurityConfiguration 导入下面两个类
     - AuthorizationServerEndpointsConfiguration  注入TokenKeyEndpointRegistrar，AuthorizationEndpoint，TokenEndpoint
     - ClientDetailsServiceConfigurer   注入ClientDetailsServiceConfigurer和ClientDetailsService
     - AuthorizationServerConfigurer  我们实现的配置类

## 1.认证服务器AuthorizationServer



```java
//OAuth2AutoConfiguration.class
...
//导入配置类
@Import({
    //根据条件，这个类不起作用
    OAuth2AuthorizationServerConfiguration.class, 
    OAuth2MethodSecurityConfiguration.class,
    //这个类起作用
    OAuth2ResourceServerConfiguration.class, 	       OAuth2RestOperationsConfiguration.class})
    public class OAuth2AutoConfiguration {
    private final OAuth2ClientProperties credentials;

    public OAuth2AutoConfiguration(OAuth2ClientProperties credentials) {
        this.credentials = credentials;
    }

    @Bean
    public ResourceServerProperties resourceServerProperties() {
        return new ResourceServerProperties(this.credentials.getClientId(), this.credentials.getClientSecret());
    }
}
```

OAuth2AuthorizationServerConfiguration不起作用，那么我们换个角度，到@EnableAuthorizationServer看看。

```java
@Import({
    	 AuthorizationServerEndpointsConfiguration.class,  
   		 AuthorizationServerSecurityConfiguration.class})
public @interface EnableAuthorizationServer {
}

@Import({ClientDetailsServiceConfiguration.class, AuthorizationServerEndpointsConfiguration.class})
public class AuthorizationServerSecurityConfiguration extends WebSecurityConfigurerAdapter {
```

AuthorizationServerEndpointsConfiguration初始化

```java
@Configuration
@Import({AuthorizationServerEndpointsConfiguration.TokenKeyEndpointRegistrar.class})
public class AuthorizationServerEndpointsConfiguration {
    private AuthorizationServerEndpointsConfigurer endpoints = new AuthorizationServerEndpointsConfigurer();
    @Autowired
    private ClientDetailsService clientDetailsService;
    //这里注入了我们的配置类，如果没配置，则注入默认的OAuth2AuthorizationServerConfiguration
    @Autowired
    private List<AuthorizationServerConfigurer> configurers = Collections.emptyList();

    ...

    @PostConstruct  //spring钩子,在构造函数后执行
    public void init() {
        Iterator var1 = this.configurers.iterator();

        while(var1.hasNext()) {
            AuthorizationServerConfigurer configurer = (AuthorizationServerConfigurer)var1.next();

            try {
                configurer.configure(this.endpoints);
            } catch (Exception var4) {
                throw new IllegalStateException("Cannot configure enpdoints", var4);
            }
        }
		//设置clientDetailsService
        this.endpoints.setClientDetailsService(this.clientDetailsService);
    }
```

```java
//configurer.configure(this.endpoints);这是我们自己配置的，给endpoints设置authenticationManager
@Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }
```

AuthorizationServerSecurityConfiguration初始化

```java
@Autowired
    public void configure(ClientDetailsServiceConfigurer clientDetails) throws Exception {
        Iterator var2 = this.configurers.iterator();

        while(var2.hasNext()) {
            AuthorizationServerConfigurer configurer = (AuthorizationServerConfigurer)var2.next();
            configurer.configure(clientDetails);
        }

    }
```

```java
//configurer.configure(clientDetails);这是我们自己配置的，创建ClientBuilder
 @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client").secret("123456").scopes("read")
                .authorizedGrantTypes("authorization_code")
                .redirectUris("https://www.getpostman.com/oauth2/callback");
    }
```

初始化完成后就到了FilterChainProxy的创建，this.webSecurity.build();

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\Oauth2.0的 webSecurityConfigureres.png)

```java
//AbstractConfiguredSecurityBuilder
protected final O doBuild() throws Exception {
        synchronized(this.configurers) {
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.INITIALIZING;
            this.beforeInit();
            this.init();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.CONFIGURING;
            this.beforeConfigure();
            this.configure();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILDING;
            O result = this.performBuild();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILT;
            return result;
        }
    }
//WebSecurityConfigurerAdapter
public void init(final WebSecurity web) throws Exception {
        final HttpSecurity http = this.getHttp();
        web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
            public void run() {
                FilterSecurityInterceptor securityInterceptor = (FilterSecurityInterceptor)http.getSharedObject(FilterSecurityInterceptor.class);
                web.securityInterceptor(securityInterceptor);
            }
        });
    }
//this.getHttp()
protected final HttpSecurity getHttp() throws Exception {
        if (this.http != null) {
            return this.http;
        } else {
            DefaultAuthenticationEventPublisher eventPublisher = (DefaultAuthenticationEventPublisher)this.objectPostProcessor.postProcess(new DefaultAuthenticationEventPublisher());
            this.localConfigureAuthenticationBldr.authenticationEventPublisher(eventPublisher);
            //1.AuthorizationServerSecurityConfiguration返回null
            //2.ResourceServerConfiguration
            //3.自定义的security配置类
            AuthenticationManager authenticationManager = this.authenticationManager();
            this.authenticationBuilder.parentAuthenticationManager(authenticationManager);
            Map<Class<? extends Object>, Object> sharedObjects = this.createSharedObjects();
            this.http = new HttpSecurity(this.objectPostProcessor, this.authenticationBuilder, sharedObjects);
            if (!this.disableDefaults) {
                ((HttpSecurity)((DefaultLoginPageConfigurer)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)this.http.csrf().and()).addFilter(new WebAsyncManagerIntegrationFilter()).exceptionHandling().and()).headers().and()).sessionManagement().and()).securityContext().and()).requestCache().and()).anonymous().and()).servletApi().and()).apply(new DefaultLoginPageConfigurer())).and()).logout();
                ClassLoader classLoader = this.context.getClassLoader();
                List<AbstractHttpConfigurer> defaultHttpConfigurers = SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);
                Iterator var6 = defaultHttpConfigurers.iterator();

                while(var6.hasNext()) {
                    AbstractHttpConfigurer configurer = (AbstractHttpConfigurer)var6.next();
                    this.http.apply(configurer);
                }
            }

            this.configure(this.http);
            return this.http;
        }
    }
```



```java
//init(this)

```





## 2.资源服务器ResourceServer

## 3.sql

```sql
/*
   role表
*/
create table role(
id bigint(20) not null auto_increment,
name varchar(255) COLLATE utf8_bin not null,
PRIMARY key (id)
)ENGINE = InnoDB auto_increment=3  DEFAULT CHARSET=utf8 collate=utf8_bin;
/*
   user表
*/
create table user(
id bigint(20) not null auto_increment,
username varchar(255) COLLATE utf8_bin not null,
password varchar(255) COLLATE utf8_bin default null,
PRIMARY key (id),
UNIQUE key (username)
)ENGINE = InnoDB auto_increment=3  DEFAULT CHARSET=utf8 collate=utf8_bin;
/*
   user_role表
*/
create table user_role(
user_id bigint(20) not null,
role_id bigint(20) not null,
FOREIGN key (user_id) REFERENCES user(id),
FOREIGN key (role_id) REFERENCES role(id)
)ENGINE = InnoDB DEFAULT CHARSET=utf8 collate=utf8_bin;


/*
   clientdetails表
*/
create table  clientdetails(
appId varchar(128) not null,
resourceIds varchar(256) default null,
appSecret varchar(256) default null,
scope varchar(256) default null,
grantTypes varchar(256) default null,
redirectUrl varchar(256) default null,
authorities varchar(256) default null,
access_token_validity INT(11) default null,
refresh_token_validity int(11) default null,
additionalInfomation varchar(4096) default null,
autoApproveScopes varchar(256) default null,
PRIMARY key (appId)
)ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*
   oauth_access_token表
*/
create table oauth_access_token(
token_id varchar(256) default null,
token blob,
authentication_id varchar(128) not null,
user_name varchar(256) default null,
client_id varchar(256) default null,
authentication blob,    
refresh_token varchar(256) default null,
primary key (authentication_id)
)ENGINE = InnoDB DEFAULT CHARSET=utf8;
/*
   oauth_approvals表
*/
create table oauth_approvals (
userId VARCHAR(256) default null,
clientId VARCHAR(256) default null,
scope VARCHAR(256) default null,
status varchar(10) default null,
expireAt datetime default null,
lastModifiedAt datetime default null
)
/*
   oauth_client_details表
*/
create table oauth_client_details(
client_id  VARCHAR(256) default null,
resource_ids VARCHAR(256) default null,
client_secret VARCHAR(256) default null,
scope VARCHAR(256) default null,
authoritized_grant_types  VARCHAR(256) default null,
web_server_redirect_url VARCHAR(256) default null,
authorities VARCHAR(256) default null,
access_token_validity int(11) default null,
refresh_token_validity int(11) default null,
additional_infomation VARCHAR(256) default null,
autoapprove VARCHAR(256) default null,
primary key (client_id)
)ENGINE = InnoDB DEFAULT CHARSET=utf8;
/*
   oauth_client_token表
*/
create table oauth_client_token(
token_id varchar(256) default null,
token blob,
authentication_id varchar(128) not null,
user_name varchar(256) default null,
client_id varchar(256) default null,
primary key (authentication_id)
)ENGINE = InnoDB DEFAULT CHARSET=utf8;
/*
   oauth_code表
*/
create table oauth_code(
code varchar(256) DEFAULT null,
authentication blob
)ENGINE = InnoDB DEFAULT CHARSET=utf8;
/*
   oauth_refresh_token表
*/
create table oauth_refresh_token(
token_id varchar(256) default null,
token blob,
authentication_id varchar(128) not null
)ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*
   数据
*/
INSERT into user(id,username,password) VALUES(1,'forezp','123456');
INSERT into user(id,username,password) VALUES(2,'admin','123456');
insert into role(id,name) values(1,'ROLE_USER');
insert into role(id,name) values(2,'ROLE_ADMIN');
INSERT into user_role(user_id,role_id) VALUES(1,1);
INSERT into user_role(user_id,role_id) VALUES(1,2);
INSERT into user_role(user_id,role_id) VALUES(2,1);
INSERT into user_role(user_id,role_id) VALUES(2,2);


```



## 4.实例详解

### 1.Authorization Server

1.在实现了==AuthorizationServerConfigurer==接口上的类加上@EnableAuthorizationServer

- ClientDetailsServiceConfigurer:配置客户端信息
  - clientId: 唯一
  - secret:
  - scope:
  - `authorizatedGrantTypes`: 认证类型
  - `authorities`: 权限信息
- AuthorizationServerEndpointsConfigurer:配置授权Token的节点和Token服务
  - `authenticationManager`: 密码验证
  - `userDetailService`: 配置获取用户验证信息的接口
  - `authorizationCodeServices`: 配置验证码服务
  - `implicitGrantService`: 配置管理implict验证的状态
  - `tokenGranter`: 配置Token Granter( ==token管理策略==如下 )
    - InmemoryTokenStore: Token存储在内存中
    - JdbcTokenStore: Token存储在数据库中
    - JwtTokenStore: 采用JWT形式,不做存储,jwt本身包含用户验证所有信息
- AuthorizationServerSecurityConfigurer:如果资源服务器和授权服务器是在同一服务中,使用默认配置即可,不在同一服务中则需要采用RemoteTokenServices(远程Token校验),资源服务器的每次请求携带的Token都需要从授权服务器作校验,这时需要配置"`/oauth/check_token`"校验节点的校验策略.

2.以bean的形式注入IOC容器,即@configuration

### 2.Resource Server

1.在实现了==ResourceServerConfigurer==接口上的类加上@EnableResourceServer

- tokenServices: 配置如何编码解码.如果Resource Server和Authorization Server在同一个工程上不需要配置.也可以用RemoteTokenServers,即Resource Server采用远程授权服务器进行Token解码,这时也不需要此配置。
- resourceId: 配置资源ID。

2.以bean的形式注入IOC容器,即@configuration

### 3.OAuth2 Client

1.Protected Resource Configuration(受保护资源配置)

使用OAuth2ProtectedResourceDetails类型的Bean来定义受保护资源,受保护资源属性如下：

- Id：资源的Id,spring oauth2没用到,默认即可
- clientId：OAuth2 Client的Id
- clientSecret：客户端密码
- accessTokenUri：获取Token的URI节点
- scope：客户端的域
- clientAuthenticationScheme：又两种客户端验证类型,分别为Http Basic 和 Form，默认为Basic
- userAuthorizationUri：如果用户需要授权访问资源，则用户将被重定向到认证url

2.Client Configuration(客户端配置) 

对于OAuth2 Client的配置，可以使用@EnableOAuth2Clien注解来简化配置，另外嗨需要配置一下两项：

- 创建一个过滤器Bean（Bean的Id为oauth2ClientContextFilter），用来存储当前请求和上下文的请求。在请求期间，如果用户需要进行身份验证，则用户会被重定向到认证url。
- 在Request域内创建AccessTokenRequest类型的Bean。













