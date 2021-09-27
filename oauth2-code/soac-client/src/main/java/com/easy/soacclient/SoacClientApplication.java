package com.easy.soacclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author 28236
 */
@SpringBootApplication
public class SoacClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoacClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
