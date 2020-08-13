# 				Oauth2.0 client源码解析

#### 首先,我们来看看流程。

(网上很多博客写的太抽象，我看了好多遍都没能理解，以下是我经过一遍一遍的调试得出)

```yaml
1.我们在浏览器输入client地址: http://localhost:8000/user
  client首先会进入filter进行校验,然后在FilterSecurityInterceptor抛出异常,重定向到			  http://localhost:8000/login并且保留原地址。
  然后会再进入filter进行校验,在Oauth2ClientAuthenticationProcessingFilter.attemptAuthentication(request, response)又抛出异常,该异常会被OAuth2ClientContextFilter捕获然后执行this.redirectUser(redirect, request, response)重定向到配置的user-authorization-uri.
  到验证服务器:http://localhost:20000/auth/authorize,开始验证,同样在FilterSecurityInterceptor抛出异常,重定向到http://localhost:20000/auth/login,再进行验证,发现是/login路径,返回登陆页。
2.我们输入用户名密码,再次验证,如果验证成功回调http://localhost:20000/auth/authorize,再回调http://localhost:8000/login?scope=...&state=...,而后client会调用http://localhost:20000/auth/oauth/token，再到http://localhost:20000/auth/oauth/check_token

  
```



```yaml
#@EnableGlobalMethodSecurity,GlobalMethodSecuritySelector.selectImports()
org.springframework.security.config.annotation.method.configuration.MethodSecurityMetadataSourceAdvisorRegistrar
org.springframework.context.annotation.AutoProxyRegistrar
org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
#client核心过滤器
org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter
org.springframework.security.oauth2.client.filter.Oauth2ClientAuthenticationProcessingFilter
#Oauth2.0上下文
org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
#配置
org.springframework.security.oauth2.client.OAuth2ProtectedResourceDetailsConfiguration
```

@EnableOAuth2Sso

```java
@EnableOAuth2Client
@EnableConfigurationProperties({OAuth2SsoProperties.class})
@Import({OAuth2SsoDefaultConfiguration.class, OAuth2SsoCustomConfiguration.class, ResourceServerTokenServicesConfiguration.class})
public @interface EnableOAuth2Sso {
}
```

@EnableOAuth2Client

```java
@Import({OAuth2ClientConfiguration.class})
public @interface EnableOAuth2Client {
}

//OAuth2ClientConfiguration
@Configuration
public class OAuth2ClientConfiguration {
    public OAuth2ClientConfiguration() {
    }

    @Bean
    public OAuth2ClientContextFilter oauth2ClientContextFilter() {
        OAuth2ClientContextFilter filter = new OAuth2ClientContextFilter();
        return filter;
    }

    @Bean
    @Scope(
        value = "request",
        proxyMode = ScopedProxyMode.INTERFACES
    )
    protected AccessTokenRequest accessTokenRequest(@Value("#{request.parameterMap}") Map<String, String[]> parameters, @Value("#{request.getAttribute('currentUri')}") String currentUri) {
        DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(parameters);
        request.setCurrentUri(currentUri);
        return request;
    }

    @Configuration
    protected static class OAuth2ClientContextConfiguration {
        @Resource
        @Qualifier("accessTokenRequest")
        private AccessTokenRequest accessTokenRequest;

        protected OAuth2ClientContextConfiguration() {
        }

        @Bean
        @Scope(
            value = "session",
            proxyMode = ScopedProxyMode.INTERFACES
        )
        public OAuth2ClientContext oauth2ClientContext() {
            return new DefaultOAuth2ClientContext(this.accessTokenRequest);
        }
    }
}

```

