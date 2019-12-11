package com.fosterpet.cmlx.commons.springextension.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fosterpet.cmlx.commons.jacksonextension.UnifyAnnotationIntrospector;
import com.fosterpet.cmlx.commons.jacksonextension.UnifyFilterProvider;
import com.fosterpet.cmlx.commons.model.PropertyFilterInfo;
import com.fosterpet.cmlx.commons.support.JsonUtility;
import org.apache.http.entity.ContentType;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @Desc 返回值视图包装器
 * @Author cmlx
 * @Date 2019-12-3 0003 10:35
 */
public class UnifyView extends AbstractView {

    // 代码
    public static final String CODE = "code";
    public static final String DATA = "data";
    // 组件模块
    public static final String THROWTYPE = "throwType";
    // 消息
    public static final String MESSAGE = "message";
    // 字段
    public static final String FIELD = "field";

    private static final Log log = LoggerFactory.make();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ContentType CONTENT_TYPE = ContentType.APPLICATION_JSON;

    public static final UnifyFilterProvider DEFAULT_FILTER_PROVIDER = new UnifyFilterProvider();
    //  暂时不统一过滤
    //  public static final String[] DEFAULT_EXCLUDE_PROPERTIES = {"createTime", "dataState", "class"};

    public static final String[] DEFAULT_EXCLUDE_PROPERTIES = {};

    protected Set<String> renderedAttributes;
    protected boolean disableCaching = true;
    protected boolean updateContentLength = false;
    protected boolean extractValueFromSingleKeyModel = false;
    protected UnifyFilterProvider filterProvider;

    static {
        //序列化枚举是否以ordinal()来输出，默认false
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        //是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //序列化过滤属性值为空的字段
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        OBJECT_MAPPER.setAnnotationIntrospector(new UnifyAnnotationIntrospector());
        DEFAULT_FILTER_PROVIDER.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(DEFAULT_EXCLUDE_PROPERTIES));
    }

    public UnifyView(PropertyFilterInfo... filters) {
        if (null == filters) {
            this.filterProvider = DEFAULT_FILTER_PROVIDER;
        } else {
            this.filterProvider = createProvider(filters);
        }
        setContentType(CONTENT_TYPE.getMimeType());
        setExposePathVariables(false);
    }

    private UnifyFilterProvider createProvider(PropertyFilterInfo[] filters) {
        List<String> include = new ArrayList<>();
        List<String> exclude = new ArrayList<>();
        List<PropertyFilterInfo> classFilters = new ArrayList<>();
        UnifyFilterProvider provider = new UnifyFilterProvider();
        for (PropertyFilterInfo filterInfo : filters) {
            Class<?> clazz = filterInfo.get_clazz();
            boolean includeFlag = filterInfo.is_include();
            if (null != clazz) {
                classFilters.add(filterInfo);
            } else {
                if (includeFlag) {
                    include.addAll(CollectionUtils.arrayToList(filterInfo.get_properties()));
                } else {
                    exclude.addAll(CollectionUtils.arrayToList(filterInfo.get_properties()));
                }
            }
        }
        String[] includeNames = include.toArray(new String[include.size()]);
        String[] excludeNames = exclude.toArray(new String[exclude.size()]);

        for (PropertyFilterInfo filterInfo : classFilters) {
            boolean includeFlag = filterInfo.is_include();
            if (includeFlag) {
                provider.addFilter(filterInfo.get_clazz(), SimpleBeanPropertyFilter.filterOutAllExcept(JsonUtility.addAll(filterInfo.get_properties(), includeNames)));
            } else {
                provider.addFilter(filterInfo.get_clazz(), SimpleBeanPropertyFilter.serializeAllExcept(JsonUtility.addAll(JsonUtility.addAll(filterInfo.get_properties(), excludeNames), DEFAULT_EXCLUDE_PROPERTIES)));
            }

        }

        PropertyFilter defaultFilter;
        // 如果包含全局包含字段，直接设置默认provider为FilterOut
        if (!CollectionUtils.isEmpty(include)) {
            provider.addFilter(UnifyViewMap.class, SimpleBeanPropertyFilter.filterOutAllExcept(CODE,DATA,THROWTYPE,MESSAGE,FIELD));
            defaultFilter = SimpleBeanPropertyFilter.filterOutAllExcept(include.toArray(new String[include.size()]));
        } else {
            String[] strings = JsonUtility.addAll(exclude.toArray(new String[exclude.size()]), DEFAULT_EXCLUDE_PROPERTIES);
            defaultFilter = SimpleBeanPropertyFilter.serializeAllExcept(strings);
        }

        provider.setDefaultFilter(defaultFilter);
        return provider;
    }

    public UnifyView(UnifyFilterProvider filterProvider) {
        if (null == filterProvider) {
            filterProvider = DEFAULT_FILTER_PROVIDER;
        }
        this.filterProvider = filterProvider;
        setContentType(CONTENT_TYPE.getMimeType());
        setExposePathVariables(false);
    }


    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Object value = filterModel(map);

        String text = OBJECT_MAPPER.writer(filterProvider).writeValueAsString(value);
        byte[] bytes = text.getBytes(CONTENT_TYPE.getCharset());

        OutputStream stream = this.updateContentLength ? createTemporaryOutputStream() : httpServletResponse.getOutputStream();
        stream.write(bytes);

        if (this.updateContentLength) {
            writeToResponse(httpServletResponse, (ByteArrayOutputStream) stream);
        }
    }

    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = new UnifyViewMap<>(model.size());
        Set<String> renderedAttributes = !CollectionUtils.isEmpty(this.renderedAttributes) ? this.renderedAttributes : model.keySet();
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        if (extractValueFromSingleKeyModel) {
            if (result.size() == 1) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    return entry.getValue();
                }
            }
        }
        return result;
    }
    @Override
    protected Map<String, Object> createMergedOutputModel(Map<String, ?> model, HttpServletRequest request,
                                                          HttpServletResponse response) {
        Map<String, Object> pathVars = (isExposePathVariables() ?
                (Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null);

        if (CollectionUtils.isEmpty(this.renderedAttributes)) {
            this.renderedAttributes = getStaticAttributes().keySet();
        }

        int size = getStaticAttributes().size();
        size += (model != null ? model.size() : 0);
        size += (pathVars != null ? pathVars.size() : 0);

        Map<String, Object> mergedModel = new LinkedHashMap<>(size);
        mergedModel.putAll(getStaticAttributes());
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }

        if (getRequestContextAttribute() != null) {
            mergedModel.put(getRequestContextAttribute(), createRequestContext(request, response, mergedModel));
        }

        return mergedModel;
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);
        response.setCharacterEncoding(CONTENT_TYPE.getCharset().name());
        if (this.disableCaching) {
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
            response.addDateHeader("Expires", 1L);
        }
    }

    public void setRenderedAttributes(Set<String> renderedAttributes) {
        this.renderedAttributes = renderedAttributes;
    }

    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public void setUpdateContentLength(boolean updateContentLength) {
        this.updateContentLength = updateContentLength;
    }
}
