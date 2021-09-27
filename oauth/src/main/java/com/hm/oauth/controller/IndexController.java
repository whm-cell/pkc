package com.hm.oauth.controller;


import com.hm.oauth.dto.LoginDto;
import com.hm.oauth.service.IUserService;
import com.hm.oauth.utils.OauthClassUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author 28236
 */
@RestController
@Api(tags = {"登录测试"})
public class IndexController {


    @Autowired
    private IUserService iUserService;

    @Autowired
    private OauthClassUtil oauthClassUtil;

    @PostMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @PostMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    @ApiOperation("登录")
    public String login(/*@ModelAttribute*/ @RequestBody @ApiParam(name="用户对象",value="传入json格式",required=true) LoginDto dto) {



        oauthClassUtil.send(dto);


        return "login";
    }

    @PostMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/401")
    public String accessDenied() {
        return "401";
    }

    @GetMapping("/user/common")
    public String common() {
        return "user/common";
    }

    @GetMapping("/user/admin")
    public String admin() {
        return "user/admin";
    }
}
