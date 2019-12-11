package com.fosterpet.cmlx.commons.aop.dm;

import com.fosterpet.cmlx.commons.constant.Constant;

import java.lang.annotation.*;

/**
 * @Desc 访问token校验
 * @Author cmlx
 * @Date 2019-12-9 0009 17:45
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessToken {

    Constant.TokenType[] value();

}
