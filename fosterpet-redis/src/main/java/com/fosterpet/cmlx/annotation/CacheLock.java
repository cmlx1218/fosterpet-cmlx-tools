package com.fosterpet.cmlx.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-10-30 0030 15:52
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheLock {

    @AliasFor("lockName")
    String value() default "";                          // 锁名（默认方法名）

    @AliasFor("value")
    String lockName() default "";

    String[] args() default {};                         // 锁方法参数序列{id, name....}

    String lockedPrefix() default "LOCK_DEFAULT";       // 锁名前缀

    long timeOut() default 3 * 1000;                    // 取锁超时时间（毫秒）

    long expireTime() default 60 * 1000;                // 锁异常过期时间（毫秒）


}
