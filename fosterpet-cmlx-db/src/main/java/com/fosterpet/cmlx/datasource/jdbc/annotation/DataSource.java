package com.fosterpet.cmlx.datasource.jdbc.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Desc 多数据源切换类
 * @Author cmlx
 * @Date 2019-12-5 0005 17:15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    @AliasFor(attribute = "key")
    String value() default "default";

    @AliasFor(attribute = "value")
    String key() default "default";
}
