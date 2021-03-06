package com.esay.soacserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String RESOURCE_ID = "account";

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    //@Autowired
    //private UserDetailsService userDetailsService;

    //@Autowired
    //RedisConnectionFactory redisConnectionFactory;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")
                .resourceIds(RESOURCE_ID)
                .authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
                .authorities("ROLE_CLIENT")
                .scopes("get_user_info", "get_childlist")
                .secret("secret")
                .redirectUris("http://localhost:2002/client/account/redirect")
                .autoApprove(true)
                .autoApprove("get_user_info")
                .and()

                .withClient("client2")
                .resourceIds(RESOURCE_ID)
                .authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
                .authorities("ROLE_CLIENT")
                .scopes("get_user_info", "get_childlist")
                .secret("secret")
                .redirectUris("http://localhost:2003/client/account/redirect");
    }

    @Bean
    public ApprovalStore approvalStore() {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore());
        return store;
    }

//    @Autowired
//    RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
        // redis??????
        // return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore())
                // ????????????
                //.userDetailsService(userDetailsService)
                // token??????redis
                //.tokenStore(new RedisTokenStore(redisConnectionFactory))
                // ??????oauth2??????
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
        ;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.realm(RESOURCE_ID).allowFormAuthenticationForClients();
    }
}