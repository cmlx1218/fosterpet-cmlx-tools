package com.fosterpet.cmlx.commons.exception.extension;

import javafx.util.Builder;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-6 0006 18:01
 */
public class ServiceRollbackException extends RuntimeException implements ExceptionInterface {

    private final String throwType;

    private final int code;

    private final Map<String, String> fields;

    private final String messageTemplate;

    private final Map<String, Object> messageParameters;

    ServiceRollbackException(String throwType, int code, Map<String, String> fields, String message, String messageTemplate, Map<String, Object> messageParameters) {
        super(message);
        this.throwType = throwType;
        this.code = code;
        this.fields = fields;
        this.messageTemplate = messageTemplate;
        this.messageParameters = messageParameters;
    }

    @Override
    public String getThrowType() {
        return throwType;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public Map<String, Object> getMessageParameters() {
        return messageParameters;
    }

    @Override
    public String getExceptionMessage() {
        return super.getLocalizedMessage();
    }

    public static Builder custom() {
        return new Builder();
    }

    public static class Builder implements ServiceExceptionBuilder {
        private static final String PATTERN = "^\\{.+\\}$";

        private String throwType;
        private int code;
        private Map<String, String> fields;
        private String messageTemplate;
        private Map<String, Object> messageParameters;
        private String exceptionMessage;

        Builder() {
            throwType = "unknown";
        }

        @Override
        public Builder setThrowType(String throwType) {
            this.throwType = throwType;
            return this;
        }

        @Override
        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        @Override
        public Builder setFields(Map<String, String> fields) {
            this.fields = fields;
            return this;
        }

        @Override
        public Builder setMessage(String message) {
            Pattern compile = Pattern.compile(PATTERN);
            Matcher matcher = compile.matcher(message);
            if (matcher.matches()) {
                this.messageTemplate = message;
            } else {
                this.exceptionMessage = message;
            }
            return this;
        }

        @Override
        public Builder setMessageParameters(Map<String, Object> messageParameters) {
            this.messageParameters = messageParameters;
            return this;
        }

        @Override
        public Exception build() {
            /*StringBuilder builder = new StringBuilder();
            builder.append("throwType [").append(throwType).append("] ");
            builder.append("code [").append(code == 0 ? "" : code).append("] ");
            builder.append("message [").append(null == messageTemplate ? exceptionMessage : messageTemplate).append("] ");*/
            //return new ServiceRollbackException(throwType, code, fields, builder.toString(), messageTemplate, messageParameters);
            return new ServiceRollbackException(throwType, code, fields, exceptionMessage, messageTemplate, messageParameters);
        }

    }
}
