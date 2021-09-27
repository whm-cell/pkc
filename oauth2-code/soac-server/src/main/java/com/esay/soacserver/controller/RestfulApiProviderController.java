package com.esay.soacserver.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
@Api(tags = {"授权码服务端测试"})
public class RestfulApiProviderController {

    @RequestMapping("/info/{account}")
    @ApiOperation("获取信息")
    public Account info(@PathVariable("account") String account) {
        return InMemoryDatabase.database.get(account);
    }

    @RequestMapping("child/{account}")
    @ApiOperation("获取信息child")
    public List<Account> child(@PathVariable("account") String qq) {
        return InMemoryDatabase.database.get(qq).getChildAccount();
    }
}