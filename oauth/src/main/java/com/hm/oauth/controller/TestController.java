package com.hm.oauth.controller;

import com.alibaba.fastjson.JSONObject;
import com.hm.oauth.dto.LoginDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api(tags = {"oauth测试"})
public class TestController {

    @GetMapping("/product/{id}")
    @ApiOperation("product")
    public String getProduct(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("isAuthenticated:{},name:{}", authentication.isAuthenticated(), authentication.getName());
        return "product id : " + id;
    }

    @GetMapping("/order/{id}")
    @ApiOperation("order")
    public String getOrder(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("isAuthenticated:{},name:{}", authentication.isAuthenticated(), authentication.getName());
        return "order id : " + id;
    }

    @PostMapping("/order/a")
    @ApiOperation("order1")
    public String order1(@RequestBody LoginDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("isAuthenticated:{},name:{}", authentication.isAuthenticated(), authentication.getName());
        return "order id : " + JSONObject.toJSONString(dto);
    }
}