package com.hm.oauth.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 28236
 */
@Configuration
@MapperScan("com.hm.oauth.mapper")
public class MybatisPlusConfig {
}