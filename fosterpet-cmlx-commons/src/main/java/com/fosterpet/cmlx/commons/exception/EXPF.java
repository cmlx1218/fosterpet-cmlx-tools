package com.fosterpet.cmlx.commons.exception;

import com.fosterpet.cmlx.commons.constant.ErrorCode;
import com.fosterpet.cmlx.commons.exception.extension.ServiceExceptionBuilder;
import com.fosterpet.cmlx.commons.exception.extension.ServiceRollbackException;
import com.fosterpet.cmlx.commons.exception.extension.ServiceSimpleException;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-6 0006 16:24
 */
@UtilityClass
public class EXPF {

    private ServiceExceptionBuilder getBuilder(int code, String message, Map<String, Object> messageParameters, String throwType, Map<String, String> fields, boolean rollback) {
        if (rollback) {
            return ServiceRollbackException.custom().setCode(code).setMessage(message).setMessageParameters(messageParameters).setThrowType(throwType).setFields(fields);
        } else {
            return ServiceSimpleException.custom().setCode(code).setMessage(message).setMessageParameters(messageParameters).setThrowType(throwType).setFields(fields);
        }
    }

    public ServiceExceptionBuilder rollbackBuilder() {
        return getBuilder(0, null, null, null, null, true);
    }

    public ServiceExceptionBuilder simpleBuilder() {
        return getBuilder(0, null, null, null, null, false);
    }

    public Exception exception(ErrorCode code, boolean rollback) {
        return getBuilder(code.getCode(), code.getTemplate(), null, null, null, rollback).build();
    }

    public Exception exception(ErrorCode code, Map<String, Object> messageParameters, String throwType, boolean rollback) {
        return getBuilder(code.getCode(), code.getTemplate(), messageParameters, throwType, null, rollback).build();
    }

    public Exception exception(ErrorCode code, Map<String, Object> messageParameters, String throwType, Map<String, String> fields, boolean rollback) {
        return getBuilder(code.getCode(), code.getTemplate(), messageParameters, throwType, fields, rollback).build();
    }

    public Exception exception(int code, String message, boolean rollback) {
        return getBuilder(code, message, null, null, null, rollback).build();
    }

    public Exception exception(int code, String message, String throwType, boolean rollback) {
        return getBuilder(code, message, null, throwType, null, rollback).build();
    }

    public Exception exception(int code, String message, Map<String, Object> messageParameters, String throwType, boolean rollback) {
        return getBuilder(code, message, messageParameters, throwType, null, rollback).build();
    }

    public Exception exception(int code, String message, Map<String, Object> messageParameters, String throwType, Map<String, String> fields, boolean rollback) {
        return getBuilder(code, message, messageParameters, throwType, fields, rollback).build();
    }

    public Exception E300(Map<String, String> fields, boolean rollback) {
        Map<String, Object> map = new HashMap<>();
        map.put("size", fields.size());
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), map, null, fields, rollback).build();
    }

    public Exception E300(Map<String, String> fields, String throwType, boolean rollback) {
        Map<String, Object> map = new HashMap<>();
        map.put("size", fields.size());
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), map, throwType, fields, rollback).build();
    }

    public Exception E300(Map<String, Object> messageParameter, Map<String, String> fields, boolean rollback) {
        if (null != messageParameter) {
            Object object = messageParameter.get("size");
            if (null == object) {
                messageParameter.put("size", fields.size());
            }
        }
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), messageParameter, null, fields, rollback).build();
    }

    public Exception E300(Map<String, Object> messageParameter, String shrowType, Map<String, String> fields, boolean rollback) {
        if (null != messageParameter) {
            Object object = messageParameter.get("size");
            if (null == object) {
                messageParameter.put("size", fields.size());
            }
        }
        return getBuilder(ErrorCode.Parameter.getCode(), ErrorCode.Parameter.getTemplate(), messageParameter, shrowType, fields, rollback).build();
    }

    public Exception E301(String throwType, boolean rollback) {
        return getBuilder(ErrorCode.Conflict.getCode(), ErrorCode.Conflict.getTemplate(), null, throwType, null, rollback).build();
    }

    public Exception E404(boolean rollback) {
        return getBuilder(ErrorCode.ResourceNotFound.getCode(), ErrorCode.ResourceNotFound.getTemplate(), null, null, null, rollback).build();
    }

    public Exception E404(String throwType, boolean rollback) {
        return getBuilder(ErrorCode.ResourceNotFound.getCode(), ErrorCode.ResourceNotFound.getTemplate(), null, throwType, null, rollback).build();
    }

    public Exception E404(Map<String, Object> messageParameter, boolean rollback) {
        return getBuilder(ErrorCode.ResourceNotFound.getCode(), ErrorCode.ResourceNotFound.getTemplate(), messageParameter, null, null, rollback).build();
    }

    public Exception E500(String throwType, boolean rollback) {
        return getBuilder(ErrorCode.Server.getCode(), ErrorCode.Server.getTemplate(), null, throwType, null, rollback).build();
    }

    /**
     * 获取异常日志
     *
     * @param e
     * @return
     */
    public String getExceptionMsg(Exception e) {
        StringBuilder sOut = new StringBuilder();
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut.append("\tat " + s + "\r\n");
        }
        return sOut.toString();
    }

    public String getThrowtype(Exception e) {
        StackTraceElement[] st = e.getStackTrace();
        StringBuilder builder = new StringBuilder("Class [");
        StackTraceElement element = st[0];
        if (st.length >= 3) {
            String className = element.getClassName();
            if (className.equals(ServiceSimpleException.Builder.class.getTypeName()) || className.equals(ServiceRollbackException.Builder.class.getTypeName())) {
                element = st[2];
            }
        }
        builder.append(element.getClassName()).append("] Method [").append(element.getMethodName()).append("]");
        return builder.toString();
    }

}
