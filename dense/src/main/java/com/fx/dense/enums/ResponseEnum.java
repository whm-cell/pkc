package com.fx.dense.enums;

/**
 * 说明：******* 代码定义范围：100-10000 *******
 * *******  错误码不能重复  *******
 *
 * @author Administrator
 */
public enum ResponseEnum {

    /**
     * 系统全局异常
     */
    UNKNOWN_ERROR(100, "未知的错误"),
    SYSTEM_ERROR(101, "系统错误"),
    PARSE_PARAMETERS_ERROR(102, "参数转换错误"),
    VALIDATE_PARAMETERS_ERROR(103, "参数校验错误"),
    SAVE_ERROR(104,"保存/更新失败"),
    DATA_DOES_NOT_EXIST(105,"数据不存在")

    ;

    private final int code;
    private final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
