package com.fosterpet.cmlx.commons.exception.translation;

import com.fosterpet.cmlx.commons.constant.ErrorCode;
import com.fosterpet.cmlx.commons.exception.TranslationContext;
import com.fosterpet.cmlx.commons.springextension.view.UnifyFailureView;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @Desc 默认异常转换器，输出code=500
 * @Author cmlx
 * @Date 2019-12-6 0006 19:57
 */
public class DefaultTranslation extends AbstractExceptionTranslation {


    @Override
    public boolean support(Exception ex) {
        return true;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        AbstractView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, ErrorCode.Server.getCode());
        view.addStaticAttribute(THROWTYPE, getThrowtype(context.getException()));
        view.addStaticAttribute(MESSAGE, interpolate(ErrorCode.Server.getTemplate(), null));
        view.addStaticAttribute(DETAILMESSAGE, context.getException().getLocalizedMessage());
        return view;
    }
}
