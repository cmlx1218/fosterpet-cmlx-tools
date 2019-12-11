package com.fosterpet.cmlx.commons.exception.translation;

import com.fosterpet.cmlx.commons.constant.ErrorCode;
import com.fosterpet.cmlx.commons.exception.TranslationContext;
import com.fosterpet.cmlx.commons.exception.annotation.ExpTranslation;
import com.fosterpet.cmlx.commons.springextension.view.UnifyFailureView;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @Desc 查询结果空异常，输出code=404
 * @Author cmlx
 * @Date 2019-12-6 0006 19:57
 */
@ExpTranslation
public class EmptyResultExceptionTranslation extends AbstractExceptionTranslation {

    @Override
    public boolean support(Exception ex) {
        return ex instanceof EmptyResultDataAccessException;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        AbstractView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, ErrorCode.ResourceNotFound.getCode());
        view.addStaticAttribute(THROWTYPE, getThrowtype(context.getException()));
        view.addStaticAttribute(MESSAGE, interpolate(ErrorCode.ResourceNotFound.getTemplate(), null));
        return view;
    }
}
