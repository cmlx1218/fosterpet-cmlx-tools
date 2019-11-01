package com.fosterpet.cmlx.annotation;

import java.lang.annotation.*;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-10-30 0030 16:35
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheRedisTTL {

    long value() default -1;    // 缓存过期时间 秒

}
