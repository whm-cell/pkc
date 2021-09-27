package com.esay.soacserver.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author whm
 * @version 1.0
 * @date 2021/7/10 14:14
 */
@Data
@ApiModel(value = "loginDto",description = "登录dto")
public class LoginDto {

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不可为空")
    private String userName;

    @ApiModelProperty("密码")
    private String password;

}
