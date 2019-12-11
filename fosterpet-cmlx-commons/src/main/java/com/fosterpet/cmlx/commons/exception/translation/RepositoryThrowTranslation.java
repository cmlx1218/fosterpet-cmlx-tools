package com.fosterpet.cmlx.commons.exception.translation;

import com.fosterpet.cmlx.commons.exception.TranslationContext;
import com.fosterpet.cmlx.commons.exception.annotation.ExpTranslation;
import com.fosterpet.cmlx.commons.exception.extension.ExceptionInterface;
import com.fosterpet.cmlx.commons.springextension.view.UnifyFailureView;
import com.fosterpet.cmlx.commons.support.StringUtility;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.springframework.web.servlet.view.AbstractView;

import java.util.InvalidPropertiesFormatException;
import java.util.Map;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-9 0009 10:30
 */
@ExpTranslation
public class RepositoryThrowTranslation extends AbstractExceptionTranslation{


    @Override
    public boolean support(Exception ex) {
        boolean b = ex instanceof InvalidPropertiesFormatException;
        if (b){
            Throwable cause = ex.getCause();
            return cause instanceof ExceptionInterface;
        }
        return false;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        ExceptionInterface anInterface = (ExceptionInterface) context.getException().getCause();
        AbstractView view = new UnifyFailureView();
        view.addStaticAttribute(CODE, anInterface.getCode());
        view.addStaticAttribute(THROWTYPE, anInterface.getThrowType());
        String messageTemplate = anInterface.getMessageTemplate();
        MessageInterpolatorContext interpolatorContext = createInterpolatorContext(anInterface.getMessageParameters());
        if (StringUtility.hasText(messageTemplate)) {
            view.addStaticAttribute(MESSAGE, interpolateByMIContext(messageTemplate, interpolatorContext));
        } else {
            view.addStaticAttribute(MESSAGE, anInterface.getExceptionMessage());
        }
        Map<String, String> fields = anInterface.getFields();
        if (null != fields) {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String value = entry.getValue();
                entry.setValue(interpolateByMIContext(value, interpolatorContext));
            }
        }
        view.addStaticAttribute(FIELDS, fields);
        return view;
    }
}
