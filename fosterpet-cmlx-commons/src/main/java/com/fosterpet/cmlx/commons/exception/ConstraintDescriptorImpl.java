package com.fosterpet.cmlx.commons.exception;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Desc Hibernate ConstrainDescriptorImpl  在转换器中使用Hibernate的模板消息转换器
 * @Author cmlx
 * @Date 2019-12-6 0006 16:25
 */
public class ConstraintDescriptorImpl<T extends Annotation> implements ConstraintDescriptor<T>, Serializable {

    private Map<String,Object> attributes;

    public ConstraintDescriptorImpl(){}

    public ConstraintDescriptorImpl(Map<String,Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public T getAnnotation() {
        return null;
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public Set<Class<?>> getGroups() {
        return null;
    }

    @Override
    public Set<Class<? extends Payload>> getPayload() {
        return null;
    }

    @Override
    public ConstraintTarget getValidationAppliesTo() {
        return null;
    }

    @Override
    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return CollectionUtils.isEmpty(attributes)?new HashMap<>(0):attributes;
    }

    @Override
    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return null;
    }

    @Override
    public boolean isReportAsSingleViolation() {
        return false;
    }


}
