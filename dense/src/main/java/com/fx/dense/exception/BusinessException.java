package com.fx.dense.exception;

import com.fx.dense.common.RestResult;
import com.fx.dense.enums.Response;
import com.fx.dense.enums.ResponseEnum;

/**
 * @author Administrator
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected final Integer code;

    protected final String message;

    public BusinessException(String message) {
        this.code = RestResult.FAILURE_CODE;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable e) {
        super(message, e);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Response response) {
        this.code = response.getCode();
        this.message = response.getMsg();
    }

    public BusinessException(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMsg();
    }

    public BusinessException(Exception e) {
        this.code = RestResult.FAILURE_CODE;
        this.message = e.getMessage();
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public Integer getCode() {
        return this.code;
    }
}
