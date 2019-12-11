package com.fosterpet.cmlx.commons.exception.extension;

import java.util.Map;

/**
 * @Desc 异常接口
 * @Author cmlx
 * @Date 2019-12-6 0006 17:16
 */
public interface ExceptionInterface {

    /**
     * 错误定位
     *
     * @return
     */
    String getThrowType();

    /**
     * 错误代码
     *
     * @return
     */
    int getCode();

    /**
     * 错误参数及错误原因
     *
     * @return
     */
    Map<String, String> getFields();

    /**
     * 错误消息模板
     *
     * @return
     */
    String getMessageTemplate();

    /**
     * 消息模板参数
     *
     * @return
     */
    Map<String, Object> getMessageParameters();

    /**
     * 异常消息
     *
     * @return
     */
    String getExceptionMessage();
}
