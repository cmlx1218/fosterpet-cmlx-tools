package com.fosterpet.cmlx.commons.springextension.view;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Desc 查询失败返回包装
 * @Author cmlx
 * @Date 2019-12-9 0009 10:52
 */
public class UnifyFailureView extends UnifyView{

    // 默认需要输出的字段 {code:'',throwType:'',message:'',fields:''}
    protected static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");
    @Getter
    @Setter
    private String[] jsonp;

    public UnifyFailureView() {
        this(null);
    }

    public UnifyFailureView(String[] jsonp) {
        super();
        this.jsonp = jsonp;
        renderedAttributes = new HashSet<>();
        renderedAttributes.add(CODE);
        renderedAttributes.add(THROWTYPE);
        renderedAttributes.add(MESSAGE);
        renderedAttributes.add(FIELD);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object value = filterModel(model);

        String text = OBJECT_MAPPER.writer(filterProvider).writeValueAsString(value);
        if (null != jsonp && jsonp.length > 0) {
            text = buildJsonpString(text, request, response);
        }
        byte[] bytes = text.getBytes(CONTENT_TYPE.getCharset());

        OutputStream stream = this.updateContentLength ? createTemporaryOutputStream() : response.getOutputStream();
        stream.write(bytes);

        if (this.updateContentLength) {
            writeToResponse(response, (ByteArrayOutputStream) stream);
        }
    }

    protected String buildJsonpString(String json, HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        for (String name : this.jsonp) {
            String value = request.getParameter(name);
            if (value != null) {
                if (!isValidJsonpQueryParam(value)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring invalid jsonp parameter value: " + value);
                    }
                    continue;
                }
                setContentType(getJsonpContentType().toString());
                setResponseContentType(request, response);
                builder.append("/**/").append(value).append("(").append(json).append(");");
                break;
            }
        }
        return builder.length() > 0 ? builder.toString() : json;
    }

    private boolean isValidJsonpQueryParam(String value) {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }

    private MediaType getJsonpContentType() {
        return new MediaType("application", "javascript");
    }

    public void addRenderedAttribute(String str) {
        renderedAttributes.add(str);
    }

    public void setRenderedAttribute(Set<String> attributes) {
        renderedAttributes = attributes;
    }


}
