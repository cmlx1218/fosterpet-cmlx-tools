package com.fosterpet.cmlx.commons.exception.translation;

import com.fosterpet.cmlx.commons.exception.TranslationContext;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @Desc 异常转换器接口
 * @Author cmlx
 * @Date 2019-12-6 0006 19:58
 */
public interface ExceptionTranslation {

    /**
     * 检查是否支持
     *
     * @param ex
     * @return
     */
    boolean support(Exception ex);

    /**
     * 转换成JSON
     *
     * @param context
     * @return
     */
    AbstractView translationToJson(TranslationContext context);


}
