package com.fx.dense.common;

import com.fx.dense.enums.Response;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class RestResult<T> implements Serializable {
    private static final long serialVersionUID = -4827004742789377839L;

    public static final Integer SUCCESS_CODE = 0;
    public static final String SUCCESS_MSG = "success";

    public static final Integer FAILURE_CODE = 1;
    public static final String FAILURE_MSG = "failure";

    private Integer code;
    private String msg;
    private T data;



    public RestResult() {}

    public RestResult(Response response) {
        this.code = response.getCode();
        this.msg = response.getMsg();
    }

    public RestResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RestResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RestResult(T data) {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MSG;
        this.data = data;
    }

    public RestResult(T data, String msg) {
        this.code = SUCCESS_CODE;
        this.msg = msg;
        this.data = data;
    }


    public RestResult success(T data) {
        this.data = data;
        return this;
    }

    public RestResult failure(T data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
        return this;
    }

    public RestResult failure(String msg) {
        this.code = FAILURE_CODE;
        this.msg = msg;
        return this;
    }

    public RestResult message(String msg) {
        this.code = SUCCESS_CODE;
        this.msg = msg;
        return this;
    }

}
