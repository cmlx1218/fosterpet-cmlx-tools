package com.fosterpet.cmlx.commons.constant;

/**
 * @Desc 异常错误码
 * @Author cmlx
 * @Date 2019-12-9 0009 10:58
 */
public enum ErrorCode {


    /**
     * ok
     */
    OK(200),

    /**
     * 服务器异常
     */
    Server(500),

    /**
     * 提交的参数有异常
     */
    Parameter(300),

    /**
     * 请求冲突或数据库唯一冲突
     */
    Conflict(301),

    /**
     * 资源未找到
     */
    ResourceNotFound(404),

    /**
     * 没有权限
     */
    AUTH(305);


    private static final String PREFIX = "{com.fosterpet.cmlx.exception.";
    private static final String SUFFIX = ".message}";
    private int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public String getTemplate() {
        return PREFIX + code + SUFFIX;
    }

}
