package com.fosterpet.cmlx.commons.exception.extension;

import java.util.Map;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-6 0006 17:35
 */
public interface ServiceExceptionBuilder {

    ServiceExceptionBuilder setThrowType(String throwType);

    ServiceExceptionBuilder setCode(int code);

    ServiceExceptionBuilder setFields(Map<String, String> fields);

    ServiceExceptionBuilder setMessage(String message);

    ServiceExceptionBuilder setMessageParameters(Map<String, Object> messageParameters);

    Exception build();

}
