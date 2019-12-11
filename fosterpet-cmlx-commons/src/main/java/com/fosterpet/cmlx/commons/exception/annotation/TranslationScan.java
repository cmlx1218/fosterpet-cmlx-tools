package com.fosterpet.cmlx.commons.exception.annotation;

import com.fosterpet.cmlx.commons.exception.TranslationRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-6 0006 16:33
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TranslationRegister.class)
public @interface TranslationScan {
    String value() default "";
}
