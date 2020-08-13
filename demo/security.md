# spring security + Oauth2.0

***

## 一.spring security

问:

1.  spring security是如何注入spring容器?
2. filter是如何加入tomcat?
3.  spring security是如何起作用?

### 1.自动注入

springboot在启动时会扫描所有jar包下的spring.factories,并且利用工具类转换成Map<String,List<String>>对象，其中org.springframework.boot.autoconfigure.EnableAutoConfiguration就是自动注入的关键。

```yml
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityRequestMatcherProviderAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
```

首先我们来看看这个类SecurityAutoConfiguration.java

```java
@Configuration
//条件，如果有这个类则注入
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class})
//自动注入参数
@EnableConfigurationProperties({SecurityProperties.class})
//导入三个配置类
@Import({SpringBootWebSecurityConfiguration.class, WebSecurityEnablerConfiguration.class, SecurityDataConfiguration.class})
public class SecurityAutoConfiguration {
    public SecurityAutoConfiguration() {
    }

    @Bean
    //容器中不存在这个类
    @ConditionalOnMissingBean({AuthenticationEventPublisher.class})
    public DefaultAuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher publisher) {
        return new DefaultAuthenticationEventPublisher(publisher);
    }
}
```

然后我们再看看三个配置类干了些什么

```java
/**
 *SpringBootWebSecurityConfiguration
 */
@Configuration
//存在这个class
@ConditionalOnClass({WebSecurityConfigurerAdapter.class})
//容器中没有这个类及其子类
@ConditionalOnMissingBean({WebSecurityConfigurerAdapter.class})
//条件是web环境
@ConditionalOnWebApplication(
    type = Type.SERVLET
)
public class SpringBootWebSecurityConfiguration {
    public SpringBootWebSecurityConfiguration() {
    }

    @Configuration
    @Order(2147483642)
    //如果以上条件成立生成默认WebSecurityConfigurerAdapter,这也是我们不做任何配置security能生效的原因
    static class DefaultConfigurerAdapter extends WebSecurityConfigurerAdapter {
        DefaultConfigurerAdapter() {
        }
    }
}



----------------------------------------------------------------------------------------
/**
 *WebSecurityEnablerConfiguration
 */
@Configuration
@ConditionalOnBean({WebSecurityConfigurerAdapter.class})
@ConditionalOnMissingBean(
    name = {"springSecurityFilterChain"}
)
@ConditionalOnWebApplication(
    type = Type.SERVLET
)
//1.引入三个类(WebSecurityConfiguration.class, SpringWebMvcImportSelector.class, OAuth2ImportSelector.class)
//2.@EnableGlobalAuthentication注解(引入AuthenticationConfiguration.class)
@EnableWebSecurity
public class WebSecurityEnablerConfiguration {
    public WebSecurityEnablerConfiguration() {
    }
}

引入的类的作用:
一.==WebSecurityConfiguration.class==
    1.注入springSecurityFilterChain，并把WebSecurityConfigurerAdapter添加到webSecurity
    2.注入SecurityExpressionHandler
    3.注入DelegatingApplicationListener

二.==SpringWebMvcImportSelector.class==
    如果有dispatcherservlet.class返回WebMvcSecurityConfiguration全类名

三.==OAuth2ImportSelector.class==
    如果存在OAuth2ClientConfiguration.class返回OAuth2ClientConfiguration全类名

四.注解@EnableGlobalAuthentication
	导入AuthenticationConfiguration.class
		1.导入ObjectPostProcessorConfiguration.class
            注入ObjectPostProcessor<Object>
		2.注入AuthenticationManagerBuilder
		3.注入GlobalAuthenticationConfigurerAdapter
---------------------------------------------------------------------------------------
/**
 *SecurityDataConfiguration
 */
@Configuration
@ConditionalOnClass({SecurityEvaluationContextExtension.class})
public class SecurityDataConfiguration {
    public SecurityDataConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    //安全表达式扩展
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}

```

接下来看看SecurityRequestMatcherProviderAutoConfiguration

```java
@Configuration
@ConditionalOnClass({RequestMatcher.class})
@ConditionalOnWebApplication(
    type = Type.SERVLET
)
public class SecurityRequestMatcherProviderAutoConfiguration {
    public SecurityRequestMatcherProviderAutoConfiguration() {
    }

    @Configuration
    @ConditionalOnClass({DispatcherServlet.class})
    @ConditionalOnBean({HandlerMappingIntrospector.class})
    public static class MvcRequestMatcherConfiguration {
        public MvcRequestMatcherConfiguration() {
        }

        @Bean
        @ConditionalOnClass({DispatcherServlet.class})
        //注入MvcRequestMatcherProvider
        public RequestMatcherProvider requestMatcherProvider(HandlerMappingIntrospector introspector) {
            return new MvcRequestMatcherProvider(introspector);
        }
    }
}

```

下面到了SecurityFilterAutoConfiguration

```java
@AutoConfigureAfter
public class SecurityFilterAutoConfiguration {
    private static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";

    @Bean
    @ConditionalOnBean(
        name = {"springSecurityFilterChain"}
    )
    //这个类实现了RegistrationBean,会在初始化时调用钩子函数onStartup(ServletContext servletContext),这个方法会最终调用父类AbstractFilterRegistrationBean的addRegistration(String description, ServletContext servletContext) 将DelegatingFilterProxy(filter的实现类)添加到ServletContext
    public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration(SecurityProperties securityProperties) {
        DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean("springSecurityFilterChain", new ServletRegistrationBean[0]);
        registration.setOrder(securityProperties.getFilter().getOrder());
        registration.setDispatcherTypes(this.getDispatcherTypes(securityProperties));
        return registration;
    }

    private EnumSet<DispatcherType> getDispatcherTypes(SecurityProperties securityProperties) {
        return securityProperties.getFilter().getDispatcherTypes() == null ? null : (EnumSet)securityProperties.getFilter().getDispatcherTypes().stream().map((type) -> {
            return DispatcherType.valueOf(type.name());
        }).collect(Collectors.collectingAndThen(Collectors.toSet(), EnumSet::copyOf));
    }
}

```



![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\AutoConfiguration.png)



tomcat启动时，TomcatStarter会遍历ServletContextInitializer[] initializers;调用所有实现ServletContextInitializer的onStartup（），而此刻会将DelegatingFilterProxy加入到ServletContext的实现类ApplicationContext的StandardContext(TomcatEmbeddedContext(为StandardContext实现类))中

```java

protected Dynamic addRegistration(String description, ServletContext servletContext) {
        Filter filter = this.getFilter();
        return servletContext.addFilter(this.getOrDeduceName(filter), filter);
}
```

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\StandardContext中的filter.png)

此时,初始化已经完毕,springSecurityFilterChain已经加入到tomcat。

tomcat结构图

```xml
<Server>顶层类元素：一个配置文件中只能有一个<Server>元素，可包含多个Service。
    <Service>顶层类元素：本身不是容器，可包含一个Engine，多个Connector。
        <Connector/>连接器类元素：代表通信接口。
        <Engine>容器类元素：为特定的Service组件处理所有客户请求，可包含多个Host。engine为顶层Container
           <Host>容器类元素：为特定的虚拟主机处理所有客户请求，可包含多个Context。
              <Context>容器类元素：为特定的Web应用处理所有客户请求。代表一个应用，包含多个Wrapper（封装了servlet）
              </Context>
           </Host>
        </Engine>
     </Service>
</Server>
```

### 2.当请求到达tomcat

首先请求会到达Acceptor类，Acceptor 继承了 AbstractEndpoint.Acceptor ，间接实现了Runnable接口，tomcat在运行时将Acceptor运行在一个后台线程内，单独监听socket请求，

```java
protected class Acceptor extends AbstractEndpoint.Acceptor {
    @Override
    public void run() {
        // Loop until we receive a shutdown command
        while (running) {
            //if we have reached max connections, wait
            countUpOrAwaitConnection();

            Socket socket = null;
            //阻塞住 监听新的socket请求
            socket = serverSocketFactory.acceptSocket(serverSocket);

            // Configure the socket
            if (running && !paused && setSocketOptions(socket)) {
                // Hand this socket off to an appropriate processor 首先使用processSocket()简单处理socket
                if (!processSocket(socket)) {
                    countDownConnection();
                    // Close socket right away
                    closeSocket(socket);
                }
            } else {
                countDownConnection();
                // Close socket right away
                closeSocket(socket);
            }

        }
    }
}

//接着进入processSocket(Socket socket)方法，processSocket方法主要工作为将socket请求信息进行封装，然后将一个实现了Runnable接口的并包含socket信息的SocketProcessor对象交给线程池，进行执行，然后Acceptor线程从该方法返回，重新监听端口上的socket请求。
protected boolean processSocket(Socket socket) {
    // Process the request from this socket
    SocketWrapper<Socket> wrapper = new SocketWrapper<Socket>(socket);
    wrapper.setKeepAliveLeft(getMaxKeepAliveRequests());
    wrapper.setSecure(isSSLEnabled());
    // During shutdown, executor may be null - avoid NPE
    if (!running) {
        return false;
    }
    getExecutor().execute(new SocketProcessor(wrapper));
    return true;
}

```

然后到达CoyoteAdapter：

CoyoteAdapter实现了对request输入流的编解码工作，并从service中获取顶层Container，将request和response交与容器组件，关键代码如下：

```java
//service(Request req, Response res)方法
connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
```

其中，在每一层容器中对请求的处理都运用了责任链模式，即所谓的Pipeline-Value处理模式，pipeline就像一个管道，表示对请求的链式处理过程，其中包含多个value，每个value代表对请求的一次处理，处理完成之后交给next Value进行处理，每层容器都至少有一个Value，这个Value被成为BaseValue，在pipeline中位于最后一个位置，比如Engine容器的BaseValue为StandardEngineValue。每一层的BaseValue会调用下一层容器的pipeline的第一个Value，由此形成一个长链,如下：

![这里写图片描述](https://img-blog.csdn.net/20170717231205517?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvam9lbnFj/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

到达pipeline长链最后一个value StandardWrapperValue后，会触发另外一个责任链模式：filterChain责任链，也就是我们平常熟悉的在web项目中配置的filter被调用的位置。StandardWrapperValve调用filterChain代码如下：

```java
//StandardWrapperValue类

public final void invoke(Request request, Response response){
    ...
    // Create the filter chain for this request
    ApplicationFilterFactory factory =
        ApplicationFilterFactory.getInstance();
    ApplicationFilterChain filterChain =
        factory.createFilterChain(request, wrapper, servlet);
    ...
    //开始调用filterChain
    filterChain.doFilter(request.getRequest(), response.getResponse());
    ...
}
```

接下来进入spring security核心类==DelegatingFilterProxyRegistrationBean==(springSecurityFilterChain)的doFilter方法，

```java
 public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Filter delegateToUse = this.delegate;
        if (delegateToUse == null) {
            synchronized(this.delegateMonitor) {
                delegateToUse = this.delegate;
                if (delegateToUse == null) {
                    //进入这里拿到AnnotationConfigServletWebServerApplicationContext
                    WebApplicationContext wac = this.findWebApplicationContext();
                    if (wac == null) {
                        throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener or DispatcherServlet registered?");
                    }
				  //该方法如下
                    delegateToUse = this.initDelegate(wac);
                }

                this.delegate = delegateToUse;
            }
        }

        this.invokeDelegate(delegateToUse, request, response, filterChain);
    }

protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
        String targetBeanName = this.getTargetBeanName();
        Assert.state(targetBeanName != null, "No target bean name set");
        Filter delegate = (Filter)wac.getBean(targetBeanName, Filter.class);
        if (this.isTargetFilterLifecycle()) {
            delegate.init(this.getFilterConfig());
        }
		//FilterChainProxy
        return delegate;
    }
//而后进入FilterChainProxy.doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) 方法中拿到filters
private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FirewalledRequest fwRequest = this.firewall.getFirewalledRequest((HttpServletRequest)request);
        HttpServletResponse fwResponse = this.firewall.getFirewalledResponse((HttpServletResponse)response);
    	//spring security14个核心过滤器
        List<Filter> filters = this.getFilters((HttpServletRequest)fwRequest);
        if (filters != null && filters.size() != 0) {
            FilterChainProxy.VirtualFilterChain vfc = new FilterChainProxy.VirtualFilterChain(fwRequest, chain, filters);
            vfc.doFilter(fwRequest, fwResponse);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(UrlUtils.buildRequestUrl(fwRequest) + (filters == null ? " has no matching filters" : " has an empty filter list"));
            }

            fwRequest.reset();
            chain.doFilter(fwRequest, fwResponse);
        }
    }
//通过VirtualFilterChain.doFilter()调用14个filter
public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (this.currentPosition == this.size) {
                if (FilterChainProxy.logger.isDebugEnabled()) {
                    FilterChainProxy.logger.debug(UrlUtils.buildRequestUrl(this.firewalledRequest) + " reached end of additional filter chain; proceeding with original chain");
                }

                this.firewalledRequest.reset();
                //当filter走完后调用这里
                this.originalChain.doFilter(request, response);
            } else {
                ++this.currentPosition;
                Filter nextFilter = (Filter)this.additionalFilters.get(this.currentPosition - 1);
                if (FilterChainProxy.logger.isDebugEnabled()) {
                    FilterChainProxy.logger.debug(UrlUtils.buildRequestUrl(this.firewalledRequest) + " at position " + this.currentPosition + " of " + this.size + " in additional filter chain; firing Filter: '" + nextFilter.getClass().getSimpleName() + "'");
                }
			   //责任链模式
                nextFilter.doFilter(request, response, this);
            }

        }
```

问：

​	==FilterChainProxy在哪里初始化？==

FilterChainProxy在WebSecurity的performBuild()中初始化

```java
protected Filter performBuild() throws Exception {
        Assert.state(!this.securityFilterChainBuilders.isEmpty(), () -> {
            return "At least one SecurityBuilder<? extends SecurityFilterChain> needs to be specified. Typically this done by adding a @Configuration that extends WebSecurityConfigurerAdapter. More advanced users can invoke " + WebSecurity.class.getSimpleName() + ".addSecurityFilterChainBuilder directly";
        });
        int chainSize = this.ignoredRequests.size() + this.securityFilterChainBuilders.size();
        List<SecurityFilterChain> securityFilterChains = new ArrayList(chainSize);
        Iterator var3 = this.ignoredRequests.iterator();

        while(var3.hasNext()) {
            RequestMatcher ignoredRequest = (RequestMatcher)var3.next();
            securityFilterChains.add(new DefaultSecurityFilterChain(ignoredRequest, new Filter[0]));
        }

        var3 = this.securityFilterChainBuilders.iterator();

        while(var3.hasNext()) {
            SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder = (SecurityBuilder)var3.next();
            //securityFilterChainBuilder.build()在这里初始化14个filter
            securityFilterChains.add(securityFilterChainBuilder.build());
        }
		//这里初始化
        FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
        if (this.httpFirewall != null) {
            filterChainProxy.setFirewall(this.httpFirewall);
        }

        filterChainProxy.afterPropertiesSet();
        Filter result = filterChainProxy;
        if (this.debugEnabled) {
            this.logger.warn("\n\n********************************************************************\n**********        Security debugging is enabled.       *************\n**********    This may include sensitive information.  *************\n**********      Do not use in a production system!     *************\n********************************************************************\n\n");
            result = new DebugFilter(filterChainProxy);
        }

        this.postBuildAction.run();
        return (Filter)result;
    }

//securityFilterChainBuilder.build().
 public final O build() throws Exception {
        if (this.building.compareAndSet(false, true)) {
            this.object = this.doBuild();
            return this.object;
        } else {
            throw new AlreadyBuiltException("This object has already been built");
        }
    }
//this.doBuild();
protected final O doBuild() throws Exception {
        synchronized(this.configurers) {
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.INITIALIZING;
            this.beforeInit();
            //1.首先得到List<SecurityConfigurer> configurers,
            this.init();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.CONFIGURING;
            this.beforeConfigure();
            //调用configurers的configure()方法，
            //14个filter在这里被添加,每个filter都是new出来的
            this.configure();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILDING;
            O result = this.performBuild();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILT;
            return result;
        }
    }

```

其实就是在SecurityAutoConfiguration   @EnableWebSecurity中导入的三个类中WebSecurityConfiguration.class的

```java
@Bean(
        name = {"springSecurityFilterChain"}
    )
	//其实就是FilterChainProxy
    public Filter springSecurityFilterChain() throws Exception {
        boolean hasConfigurers = this.webSecurityConfigurers != null && !this.webSecurityConfigurers.isEmpty();
        if (!hasConfigurers) {
            WebSecurityConfigurerAdapter adapter = (WebSecurityConfigurerAdapter)this.objectObjectPostProcessor.postProcess(new WebSecurityConfigurerAdapter() {
            });
            this.webSecurity.apply(adapter);
        }

        return (Filter)this.webSecurity.build();
    }
```

### 3.security流程

参考：

Security:     https://my.oschina.net/hutaishi/blog/1834821

Oauth2.0:   https://www.cnblogs.com/mxmbk/p/9952298.html

#### 1.认证时序图

![img](https://oscimg.oschina.net/oscnet/d91677da985e141b03c85c66a620244212d.jpg)

#### 2.授权时序图

![img](https://oscimg.oschina.net/oscnet/002997db61fa0bc08b9411eca644eca3a86.jpg)

![img](https://oscimg.oschina.net/oscnet/7d9bf91960df9a27a99647da6805572ebd0.jpg)

#### 3.filter执行顺序

1. WebAsyncManagerIntegrationFilter,设置属性为true，request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);alreadyFilteredAttributeName=org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.FILTERED

2. SecurityContextPersistenceFilter，这样的话在一开始进行request的时候就可以在SecurityContextHolder建立一个SecurityContext，然后在请求结束的时候，SecurityContext 再保存到配置好的SecurityContextRepository，任何对SecurityContext的改变都可以被copy到HttpSession。

3. HeaderWriterFilter,同一，alreadyFilteredAttributeName=org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.FILTERED

4. LogoutFilter，判断是否需要登出，否则直接进入下一个filter,是

5. UsernamePasswordAuthenticationFilter用于处理来自表单提交的认证。该表单必须提供对应的用户名和密码，其内部还有登录成功或失败后进行处理的 AuthenticationSuccessHandler 和AuthenticationFailureHandler，这些都可以根据需求做相

   关改变；

6. DefaultLoginPageGeneratingFilter,判断是不是/login,是直接返回login.html

   ```java
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
           HttpServletRequest request = (HttpServletRequest)req;
           HttpServletResponse response = (HttpServletResponse)res;
           boolean loginError = this.isErrorPage(request);
           boolean logoutSuccess = this.isLogoutSuccess(request);
           if (!this.isLoginUrlRequest(request) && !loginError && !logoutSuccess) {
               chain.doFilter(request, response);
           } else {
               String loginPageHtml = this.generateLoginPageHtml(request, loginError, logoutSuccess);
               response.setContentType("text/html;charset=UTF-8");
               response.setContentLength(loginPageHtml.getBytes(StandardCharsets.UTF_8).length);
               response.getWriter().write(loginPageHtml);
           }
       }
   ```

   

7. DefaultLogoutPageGeneratingFilter,首先同一alreadyFilteredAttributeName=org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter.FILTERED，然后判断是不是登出/logout,是重定向，不是，下一个filter

8. BasicAuthenticationFilter首先同一，然后判断header是否存在key为Authorization,如果存在，==(介入其他插件，选择token)==，如果不存在，进入下个filter

9. RequestCacheAwareFilter，取缓存里的request,如果没有，用本request

10. SecurityContextHolderAwareRequestFilter，它将会把HttpServletRequest封装成一个继承自HttpServletRequestWrapper的SecurityContextHolderAwareRequestWrapper(Servlet3SecurityContextHolderAwareRequestWrapper)，同时使用SecurityContext实现了HttpServletRequest中与安全相关的方法。

11. AnonymousAuthenticationFilter，如果之前的认证机制都没有更新SecurityContextHolder拥有的Authentication，那么一个AnonymousAuthenticationToken(即不做任何配置的时候那个用户信息)将会设给SecurityContextHolder。

12. SessionManagementFilter查看request.getAttribute("__spring_security_session_mgmt_filter_applied")，如果无则如下，有则下个filter

    - 1.设置这个值为true
    - 2.进行判断是否是AnonymousAuthentication

13. ExceptionTransactionFilter， 能够捕获来自 FilterChain 所有的异常，并进行处理。但是它只会处理两类异常：AuthenticationException 和 AccessDeniedException，其它的异常它会继续抛出。--- 如果捕获到的是 AuthenticationException，那么将会使用其对应的AuthenticationEntryPoint 的commence()处理。在处理之前，ExceptionTranslationFilter先使用 RequestCache 将当前的HttpServerletRequest的信息保存起来，以至于用户成功登录后可以跳转到之前的界面；--- 如果捕获到的是 AccessDeniedException，那么将视当前访问的用户是否已经登录认证做不同的处理，如果未登录，则会使

    用关联的 AuthenticationEntryPoint 的 commence()方法进行处理，否则将使用关联的 AccessDeniedHandler 的handle()方法进行处理。

14. FilterSecurityInterceptor，保护Web URI，并且在访问被拒绝时抛出异常，它需要一AccessDecisionManager和一个AuthenticationManager 的引用。它会从 SecurityContextHolder 获取 Authentication，然后通过 SecurityMetadataSource 可以得知当前请求是否在请求受保护的资源。对于请求那些受保护的资源，如果Authentication.isAuthenticated()返回false或者FilterSecurityInterceptor的alwaysReauthenticate 属性为 true，那么将会使用其引用的 AuthenticationManager 再认证一次，认证之后再使用认证后的 Authentication 替换 SecurityContextHolder 中拥有的那个。然后就是利用 AccessDecisionManager 进行权限的检查。

    说明：

    - AuthenticationEntryPoint 是在用户没有登录时用于引导用户进行登录认证的；

    - AccessDeniedHandler 用于在用户已经登录了，但是访问其自身没有权限的资源时做出对应的处理 [默认实现类： AccessDeniedHandlerImpl]；

    - RequestCache [默认实现类：HttpSessionRequestCache]会将 HttpServletRequest 相关信息封装为一个 SavedRequest 并保存到 HttpSession中；

FilterSecurityInterceptor执行流程；

```java
//beforeInvocation(Object object) 几个重要步骤
//1.从SecurityMetadataSource拿到Collection<ConfigAttribute>
Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource().getAttributes(object);
...
//验证
Authentication authenticated = this.authenticateIfRequired();
...
//授权    
this.accessDecisionManager.decide(authenticated, object, attributes);
```

```java
//this.authenticateIfRequired();
//默认
...
authentication = this.authenticationManager.authenticate(authentication);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Successfully Authenticated: " + authentication);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
//this.authenticationManager.authenticate(authentication)
//默认进入ProviderManager.class
...
Iterator var8 = this.getProviders().iterator();

        while(var8.hasNext()) {
            //默认只有DaoAuthenticationProvider，但它并没有重写该方法，所以进入父类AbstractUserDetailsAuthenticationProvider
            AuthenticationProvider provider = (AuthenticationProvider)var8.next();
            if (provider.supports(toTest)) {
                if (debug) {
                    logger.debug("Authentication attempt using " + provider.getClass().getName());
                }
                try {
                    result = provider.authenticate(authentication);
//AbstractUserDetailsAuthenticationProvider.authenticate(Authentication authentication)
...
String username = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
            cacheWasUsed = false;

            try {
                user = this.retrieveUser(username, (UsernamePasswordAuthenticationToken)authentication);
...
//这里是验证密码
this.preAuthenticationChecks.check(user);
this.additionalAuthenticationChecks(user(UsernamePasswordAuthenticationToken)authentication);
this.postAuthenticationChecks.check(user);
                //放入缓存
if (!cacheWasUsed) {
    this.userCache.putUserInCache(user);
}
return this.createSuccessAuthentication(principalToReturn, authentication, user);
```

```java
//this.accessDecisionManager.decide(authenticated, object, attributes);
//接下来是授权   AffirmativeBased.decide()
int deny = 0;
Iterator var5 = this.getDecisionVoters().iterator();
while(var5.hasNext()) {
     AccessDecisionVoter voter = (AccessDecisionVoter)var5.next();
     int result = voter.vote(authentication, object, configAttributes);
...
switch(result) {
            case -1:
                ++deny;
                break;
            case 1:
                return;
            }
        }

        if (deny > 0) {
            //这里抛出了异常
            throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
        } else {
            this.checkAllowIfAllAbstainDecisions();
        }    
```

```java
//如有异常 ExceptionTranslationFilter会捕捉到
 this.handleSpringSecurityException(request, response, chain, (RuntimeException)ase);

//逻辑如下 
if (exception instanceof AuthenticationException) {
            this.logger.debug("Authentication exception occurred; redirecting to authentication entry point", exception);
            this.sendStartAuthentication(request, response, chain, (AuthenticationException)exception);
        } else if (exception instanceof AccessDeniedException) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!this.authenticationTrustResolver.isAnonymous(authentication) && !this.authenticationTrustResolver.isRememberMe(authentication)) {
                this.logger.debug("Access is denied (user is not anonymous); delegating to AccessDeniedHandler", exception);
                this.accessDeniedHandler.handle(request, response, (AccessDeniedException)exception);
            } else {
                this.logger.debug("Access is denied (user is " + (this.authenticationTrustResolver.isAnonymous(authentication) ? "anonymous" : "not fully authenticated") + "); redirecting to authentication entry point", exception);
                this.sendStartAuthentication(request, response, chain, new InsufficientAuthenticationException(this.messages.getMessage("ExceptionTranslationFilter.insufficientAuthentication", "Full authentication is required to access this resource")));
            }
        }
```

问：

​	说了这么多到底如何使用(配置)呢？我们先来看看security如何解析配置

#### 4.回到FilterChainProxy的创建

```java
@Bean(
        name = {"springSecurityFilterChain"}
    )
    public Filter springSecurityFilterChain() throws Exception {
        //判断有无配置类
        boolean hasConfigurers = this.webSecurityConfigurers != null && !this.webSecurityConfigurers.isEmpty();
        if (!hasConfigurers) {
            //没有就用默认的
            WebSecurityConfigurerAdapter adapter = (WebSecurityConfigurerAdapter)this.objectObjectPostProcessor.postProcess(new WebSecurityConfigurerAdapter() {
            });
            this.webSecurity.apply(adapter);
        }

        return (Filter)this.webSecurity.build();
    }
```

问：

this.webSecurityConfigurers怎么来的呢？

```java
 @Autowired(
        required = false
    )
    public void setFilterChainProxySecurityConfigurer(ObjectPostProcessor<Object> objectPostProcessor, 
//这里通过autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()注入， 而                                                     
@Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}") List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers) throws Exception {
        this.webSecurity = (WebSecurity)objectPostProcessor.postProcess(new WebSecurity(objectPostProcessor));
        if (this.debugEnabled != null) {
            this.webSecurity.debug(this.debugEnabled);
        }

        Collections.sort(webSecurityConfigurers, WebSecurityConfiguration.AnnotationAwareOrderComparator.INSTANCE);
        Integer previousOrder = null;
        Object previousConfig = null;

        Iterator var5;
        SecurityConfigurer config;
        for(var5 = webSecurityConfigurers.iterator(); var5.hasNext(); previousConfig = config) {
            config = (SecurityConfigurer)var5.next();
            Integer order = WebSecurityConfiguration.AnnotationAwareOrderComparator.lookupOrder(config);
            if (previousOrder != null && previousOrder.equals(order)) {
                throw new IllegalStateException("@Order on WebSecurityConfigurers must be unique. Order of " + order + " was already used on " + previousConfig + ", so it cannot be used on " + config + " too.");
            }

            previousOrder = order;
        }

        var5 = webSecurityConfigurers.iterator();

        while(var5.hasNext()) {
            config = (SecurityConfigurer)var5.next();
            this.webSecurity.apply(config);
        }

        this.webSecurityConfigurers = webSecurityConfigurers;
    }
//autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()
 public List<SecurityConfigurer<Filter, WebSecurity>> getWebSecurityConfigurers() {
        List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers = new ArrayList();
     //WebSecurityConfigurerAdapter是WebSecurityConfigurer实现类
        Map<String, WebSecurityConfigurer> beansOfType = this.beanFactory.getBeansOfType(WebSecurityConfigurer.class);
        Iterator var3 = beansOfType.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<String, WebSecurityConfigurer> entry = (Entry)var3.next();
            webSecurityConfigurers.add(entry.getValue());
        }

        return webSecurityConfigurers;
    }
```

这里能看出为什么我们配置都是实现这个类**==WebSecurityConfigurerAdapter==**了吧



接下来我们进入this.webSecurity.build()一探究竟

```java
//WebSecurity实现了AbstractSecurityBuilder
public final O build() throws Exception {
        if (this.building.compareAndSet(false, true)) {
            this.object = this.doBuild();
            return this.object;
        } else {
            throw new AlreadyBuiltException("This object has already been built");
        }
    }

    protected final O doBuild() throws Exception {
        synchronized(this.configurers) {
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.INITIALIZING;
            this.beforeInit();
            this.init();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.CONFIGURING;
            this.beforeConfigure();
            this.configure();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILDING;
            //这里将产生FilterChainProxy
            O result = this.performBuild();
            this.buildState = AbstractConfiguredSecurityBuilder.BuildState.BUILT;
            return result;
        }
    }
//遍历所有WebSecurityConfigurerAdapter实现类init方法(鉴于你配置了几个)
private void init() throws Exception {
        Collection<SecurityConfigurer<O, B>> configurers = this.getConfigurers();
        Iterator var2 = configurers.iterator();

        SecurityConfigurer configurer;
        while(var2.hasNext()) {
            configurer = (SecurityConfigurer)var2.next();
            configurer.init(this);
        }

        var2 = this.configurersAddedInInitializing.iterator();

        while(var2.hasNext()) {
            configurer = (SecurityConfigurer)var2.next();
            configurer.init(this);
        }

    }
//WebSecurityConfigurerAdapter.init()
public void init(final WebSecurity web) throws Exception {
        final HttpSecurity http = this.getHttp();
        web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
            public void run() {
                FilterSecurityInterceptor securityInterceptor = (FilterSecurityInterceptor)http.getSharedObject(FilterSecurityInterceptor.class);
                web.securityInterceptor(securityInterceptor);
            }
        });
    }
//this.getHttp();
protected final HttpSecurity getHttp() throws Exception {
        if (this.http != null) {
            return this.http;
        } else {
            DefaultAuthenticationEventPublisher eventPublisher = (DefaultAuthenticationEventPublisher)this.objectPostProcessor.postProcess(new DefaultAuthenticationEventPublisher());
            this.localConfigureAuthenticationBldr.authenticationEventPublisher(eventPublisher);
            //这里如果你没有重写，则调用WebSecurityConfigurerAdapter的
            AuthenticationManager authenticationManager = this.authenticationManager();
            this.authenticationBuilder.parentAuthenticationManager(authenticationManager);
            this.authenticationBuilder.authenticationEventPublisher(eventPublisher);
            Map<Class<? extends Object>, Object> sharedObjects = this.createSharedObjects();
            this.http = new HttpSecurity(this.objectPostProcessor, this.authenticationBuilder, sharedObjects);
            if (!this.disableDefaults) {
                //这里会将剩下configurer全创建，并添加到集合类
                ((HttpSecurity)((DefaultLoginPageConfigurer)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)this.http.csrf().and()).addFilter(new WebAsyncManagerIntegrationFilter()).exceptionHandling().and()).headers().and()).sessionManagement().and()).securityContext().and()).requestCache().and()).anonymous().and()).servletApi().and()).apply(new DefaultLoginPageConfigurer())).and()).logout();
                ClassLoader classLoader = this.context.getClassLoader();
                List<AbstractHttpConfigurer> defaultHttpConfigurers = SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);
                Iterator var6 = defaultHttpConfigurers.iterator();

                while(var6.hasNext()) {
                    AbstractHttpConfigurer configurer = (AbstractHttpConfigurer)var6.next();
                    this.http.apply(configurer);
                }
            }
//这里会调用你配置的(如果你配置了，否则调用WebSecurityConfigurerAdapter的)
            this.configure(this.http);
            return this.http;
        }
    }
//this.authenticationManager();
protected AuthenticationManager authenticationManager() throws Exception {
        if (!this.authenticationManagerInitialized) {
            //这里如果你没有重写，则调用WebSecurityConfigurerAdapter的
            this.configure(this.localConfigureAuthenticationBldr);
            if (this.disableLocalConfigureAuthenticationBldr) {
                this.authenticationManager = this.authenticationConfiguration.getAuthenticationManager();
            } else {
                //这里也会初始化provider,默认为DaoAuthenticationProvider
                this.authenticationManager = (AuthenticationManager)this.localConfigureAuthenticationBldr.build();
            }

            this.authenticationManagerInitialized = true;
        }

        return this.authenticationManager;
    }
// this.configure(this.localConfigureAuthenticationBldr);这里我们重写了
 @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("da").password("123").roles("USER")
                .and()
                .withUser("admin").password(new BCryptPasswordEncoder().encode("admin")).roles("ADMIN")
        ;
//this.localConfigureAuthenticationBldr.build();再次进入this.dobuild()构建AuthenticaManager，默认返回ProvideManager
...
    
 
//我们再看产生FilterChainProxy的this.performBuild();    
//这里会    
     protected Filter performBuild() throws Exception {
        Assert.state(!this.securityFilterChainBuilders.isEmpty(), () -> {
            return "At least one SecurityBuilder<? extends SecurityFilterChain> needs to be specified. Typically this done by adding a @Configuration that extends WebSecurityConfigurerAdapter. More advanced users can invoke " + WebSecurity.class.getSimpleName() + ".addSecurityFilterChainBuilder directly";
        });
        int chainSize = this.ignoredRequests.size() + this.securityFilterChainBuilders.size();
        List<SecurityFilterChain> securityFilterChains = new ArrayList(chainSize);
        Iterator var3 = this.ignoredRequests.iterator();

        while(var3.hasNext()) {
            RequestMatcher ignoredRequest = (RequestMatcher)var3.next();
            securityFilterChains.add(new DefaultSecurityFilterChain(ignoredRequest, new Filter[0]));
        }

        var3 = this.securityFilterChainBuilders.iterator();

        while(var3.hasNext()) {
            SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder = (SecurityBuilder)var3.next();
            //这里是重点，整合所有配置
            securityFilterChains.add(securityFilterChainBuilder.build());
        }

        FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
        if (this.httpFirewall != null) {
            filterChainProxy.setFirewall(this.httpFirewall);
        }

        filterChainProxy.afterPropertiesSet();
        Filter result = filterChainProxy;
        if (this.debugEnabled) {
            this.logger.warn("\n\n********************************************************************\n**********        Security debugging is enabled.       *************\n**********    This may include sensitive information.  *************\n**********      Do not use in a production system!     *************\n********************************************************************\n\n");
            result = new DebugFilter(filterChainProxy);
        }

        this.postBuildAction.run();
        return (Filter)result;
    }      
        
```



### 4.配置方式

#### 1.配置自定义provider

```java
	//WebSecurityConfigurerAdapter实现类 	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(customAuthenticationProvider})   
    }
```

#### 2.配置自定义UserDetailsService

```java
 @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
```

#### 3.配置自定义FilterSecurityInterceptor

```java
@Override
        protected void configure(HttpSecurity http) throws Exception {
            http..authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
			//添加SecurityMetadataSource,AccessDecisionManager,AuthenticationManager
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource();//实现FilterInvocationSecurityMetadataSource通过比较URL拿到角色
                o.setAccessDecisionManager();//实现AccessDecisionManager,通过传参(角色)比较Authentication中的角色是否符合，
           
                o.setAuthenticationManager();//实现AuthenticationManager
                return o;
            }
        })
```

#### 4.配置自定义AccessDeniedHandler

```java
		@Override
        protected void configure(HttpSecurity http) throws Exception{
            http.exceptionHandling()
                .accessDeniedHandler(deniedHandler);//实现AccessDeniedHandler
        }
```



#### 5.配置其他

```java
@Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic()       .and().csrf().disable().authorizeRequests().antMatchers("/login").permitAll()
                .antMatchers("/oauth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout().permitAll();
        }
```









### 5.重要元素关系图

#### 1.SecurityBuilder关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\SecurityBuilder.png)

#### 2.AuthenticationManager关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\ProviderManager.png)

#### 3.AuthenticationProvider关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\AuthenticationProvider.png)

#### 4.UserDetails关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\User.png)

#### 5.UserDetailsService关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\UserDetailsService.png)

#### 6.SecurityMetadataSource关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\FilterInvocationSecurityMetaSource.png)

#### 7.AccessDecisionManager关系图

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\AccessDessionManager.png)

## 二.方法级别上的保护

#### 1.开启方法级别支持

```java
@EnabledWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) 
//secureEnabled = true  
//jsr250Enabled = true  
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
}
```

#### 2.使用方式

```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
@PreAuthorize("hasPermission('/user','select')")  //参数:1.路径 2.权限
```

#### 3.配置PermissionEvaluator

```java
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysRoleService roleService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        // 获得loadUserByUsername()方法的结果
        User user = (User)authentication.getPrincipal();
        // 获得loadUserByUsername()中注入的角色
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        // 遍历用户所有角色
        for(GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();
            Integer roleId = roleService.selectByName(roleName).getId();
            // 得到角色所有的权限
            List<SysPermission> permissionList = permissionService.listByRoleId(roleId);

            // 遍历permissionList
            for(SysPermission sysPermission : permissionList) {
                // 获取权限集
                List permissions = sysPermission.getPermissions();
                // 如果访问的Url和权限用户符合的话，返回true
                if(targetUrl.equals(sysPermission.getUrl())
                        && permissions.contains(targetPermission)) {
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {


    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return this.webSecurityExpressionHandler();
    }


    public DefaultMethodSecurityExpressionHandler webSecurityExpressionHandler(){
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }
}
```

```sql
create table user (
id int(11) PRIMARY key auto_increment,
username varchar(16) not null UNIQUE,
password varchar(100) not null,
address varchar(255) not null,
tel varchar(11) not null UNIQUE,
identity varchar(18) not null UNIQUE,
sex varchar(4) not null default '男',
age int(3) not null ,
name varchar(30) not null
);

create table role (
id int(11) PRIMARY KEY auto_increment,
name varchar(30) not null,
name_ZH varchar(66) comment '中文名(部门等级)'
);

create table user_role(
id int(11) PRIMARY key auto_increment,
user_id int(11),
role_id int(11) 
);

create table sys_permission(
`id` int(11) NOT NULL AUTO_INCREMENT,
`url` varchar(255) DEFAULT NULL,
`role_id` int(11) DEFAULT NULL,
`permission` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`),
foreign key (role_id) references role(id)
)ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
-- 写法2
CREATE TABLE `sys_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_roleId` (`role_id`),
  CONSTRAINT `fk_roleId` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) 
```

