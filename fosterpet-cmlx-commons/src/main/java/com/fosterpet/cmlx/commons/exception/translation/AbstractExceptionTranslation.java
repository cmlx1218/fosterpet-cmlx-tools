package com.fosterpet.cmlx.commons.exception.translation;


import com.fosterpet.cmlx.commons.exception.ConstraintDescriptorImpl;
import com.fosterpet.cmlx.commons.exception.EXPF;
import com.fosterpet.cmlx.commons.exception.TranslationContext;
import com.fosterpet.cmlx.commons.springextension.view.UnifyFailureView;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.MessageInterpolator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Desc 异常转换器抽象类
 * @Author cmlx
 * @Date 2019-12-6 0006 19:55
 */
public abstract class AbstractExceptionTranslation implements ExceptionTranslation {

    protected static final String THROWTYPE = "throwType";
    protected static final String CODE = "code";
    protected static final String MESSAGE = "message";
    protected static final String FIELDS = "fields";
    protected static final String DETAILMESSAGE = "detailMessage";

    protected static Set<String> defaultRendered;

    @Getter
    @Setter
    private MessageInterpolator interpolator;

    public AbstractExceptionTranslation() {
        defaultRendered = new HashSet<>();
        defaultRendered.add(THROWTYPE);
        defaultRendered.add(CODE);
        defaultRendered.add(MESSAGE);
        defaultRendered.add(FIELDS);
        defaultRendered.add(DETAILMESSAGE);
    }

    public AbstractView getView(TranslationContext context) {
        AbstractView jsonView = this.translationToJson(context);
        if(null != context.getJsonp() && context.getJsonp().length > 0) {
            if(jsonView instanceof UnifyFailureView){
                String[] jsonp = ((UnifyFailureView) jsonView).getJsonp();
                if(null == jsonp || jsonp.length == 0) {
                    ((UnifyFailureView) jsonView).setJsonp(context.getJsonp());
                }
            }else{
                Map<String, Object> staticAttributes = jsonView.getStaticAttributes();
                UnifyFailureView failureView = new UnifyFailureView(context.getJsonp());
                failureView.setAttributesMap(staticAttributes);
                jsonView = failureView;
            }
        }
        return jsonView;
    }
    public boolean isToJson(HttpServletRequest request) {
        return false;
    }

    public boolean isToXml(HttpServletRequest request) {
        return false;
    }

    public boolean isToHtml(HttpServletRequest request) {
        return false;
    }

    public MessageInterpolatorContext createInterpolatorContext(Map<String,Object> messageParameters) {
        if(null == messageParameters) {
            messageParameters = new HashMap<>(0);
        }
        return new MessageInterpolatorContext(new ConstraintDescriptorImpl(messageParameters),null,null,messageParameters);
    }

    public String interpolate(String messageTemplate,Map<String,Object> messageParameters) {
        MessageInterpolatorContext interpolatorContext = createInterpolatorContext(messageParameters);
        return interpolator.interpolate(messageTemplate,interpolatorContext);
    }

    public String interpolateByMIContext(String messageTemp, HibernateMessageInterpolatorContext micontext) {
        return interpolator.interpolate(messageTemp,micontext);
    }

    protected String getThrowtype(Exception e) {
        return EXPF.getThrowtype(e);
    }

    protected Throwable rootCause(Exception ex) {
        Throwable rootCause = null;
        Throwable cause = ex.getCause();
        while(cause != null && cause != rootCause){
            rootCause = cause;
            cause = cause.getCause();
        }
        return rootCause;
    }

}
