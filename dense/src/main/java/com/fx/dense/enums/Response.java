package com.fx.dense.enums;

/**
 * @author Administrator
 */
public enum Response {

    /**
     * 0 表示返回成功
     */
    SUCCESS(200,"成功"),

    /**
     * 警告
     */
    WARN(301, "被请求的资源已永久移动到新位置"),

    /**
     * 错误
     */
    ERROR(500, "系统访问异常，请联系管理员"),
    ERROR_TIMEOUT(501, "请求超时"),

    /**
     * 系统异常
     */
    RUNNING_ERROR(101,"系统运行时异常,请联系管理员"),
    PARSE_PARAMETERS_ERROR(102, "参数转换错误"),
    VALIDATE_PARAMETERS_ERROR(103, "参数校验错误"),

    ;
    private Integer code;
    private String msg;

    Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

