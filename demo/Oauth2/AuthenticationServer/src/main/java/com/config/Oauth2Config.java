package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@EnableAuthorizationServer
@Configuration
public class Oauth2Config extends AuthorizationServerConfigurerAdapter {

//    token生成策略
    @Autowired
    private TokenStore tokenStore;

//    client 客户端信息服务类
    @Autowired
    private ClientDetailsService clientDetailsService;

//    授权码服务类
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

//    身份信息管理类
    @Autowired
    private AuthenticationManager authenticationManager;

//    密码加密器
    @Autowired
    private PasswordEncoder passwordEncoder;

//    配置认证服务
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 配置客户端信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client")
                .secret(passwordEncoder.encode("123456"))
                .resourceIds("resource1") //可以访问的资源编号
                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit","refresh_token") //客户端允许的授权类型
                .scopes("server") //允许授权的范围
                .autoApprove(false) //false  表示请求来到时会跳转到授权页面
                .redirectUris("http://www.baidu.com")
        ;
    }

//    配置认证服务端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) //security认证管理器
                .tokenServices(tokenService()) //token服务
                .authorizationCodeServices(authorizationCodeServices) //授权码服务
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
        ;
    }


    /**
     * token 令牌服务
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        defaultTokenServices.setTokenStore(tokenStore);  //关联存储方式
//        defaultTokenServices.setTokenEnhancer();
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds(7200); //令牌有效期 两小时
        defaultTokenServices.setRefreshTokenValiditySeconds(259200); //刷新令牌有效期 三天
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
}
