package com.hm.oauth.utils;


import com.alibaba.fastjson.JSONObject;
import com.hm.oauth.dto.LoginDto;
import com.hm.oauth.response.TokenContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * @author whm
 * @version 1.0
 * @date 2021/7/10 16:07
 */
@Component
public class OauthClassUtil {

    @Autowired
    private RestTemplate restTemplate;

    public void send(LoginDto dto){


       // String url = "http://localhost:8080/oauth/token?username=user&password=123456&grant_type=password&scope=select&client_id=client_2&client_secret=123456";


        String urlHeader = "http://localhost:1234/oauth/token";

        MultiValueMap<String,Object> paramMap = new LinkedMultiValueMap<>();

        paramMap.add("username",dto.getUserName());
        paramMap.add("password",dto.getPassword());
        paramMap.add("grant_type","password");
        paramMap.add("scope","select");
        paramMap.add("client_id","client_2");
        paramMap.add("client_secret",123456);

        TokenContext context1 = restTemplate.postForObject(urlHeader, paramMap, TokenContext.class);

    };

}
