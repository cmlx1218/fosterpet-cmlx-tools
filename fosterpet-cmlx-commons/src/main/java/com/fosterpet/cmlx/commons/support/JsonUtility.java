package com.fosterpet.cmlx.commons.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fosterpet.cmlx.commons.jacksonextension.UnifyAnnotationIntrospector;
import com.fosterpet.cmlx.commons.jacksonextension.UnifyFilterProvider;
import com.fosterpet.cmlx.commons.model.PropertyFilterInfo;
import lombok.experimental.UtilityClass;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-3 0003 16:37
 */
@UtilityClass
public class JsonUtility{
    private final Log log = LoggerFactory.make();
    public final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();
    public final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public final UnifyFilterProvider DEFAULT_FILTER_PROVIDER = new UnifyFilterProvider();
    public final String[] DEFAULT_EXCLUDE_PROPERTIES = {"class"};

    static{
        SimpleModule module = new SimpleModule("JsonUtility",new Version(1,0,0,"SNAPSHOT","com.aimymusic.appserver","Appserver-commons"));

        module.addSerializer(float.class, new FloatSerializer());
        module.addSerializer(Float.class, new FloatSerializer());
        module.addSerializer(double.class, new DoubleSerializer());
        module.addSerializer(Double.class, new DoubleSerializer());

        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);// 过滤不需要的字段
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);     // 枚举值ordinal()输出
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);  // 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        OBJECT_MAPPER.setAnnotationIntrospector(new UnifyAnnotationIntrospector());
        DEFAULT_FILTER_PROVIDER.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(DEFAULT_EXCLUDE_PROPERTIES));
    }

    public JsonNode readTree(String json) throws Exception {
        try {
            return JsonUtility.OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            log.warnf(e,"Convert [{}] to jsonNode failed", json);
            //throw EXPF.exception(ErrorCode.JsonDeserializer,true);
            throw e;
        }
    }


    public String toString(Object object) throws Exception {
        try {
            return OBJECT_MAPPER.writer(DEFAULT_FILTER_PROVIDER).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warnf(e,"Convert [{} {}] to string failed",object != null ? object.getClass() : "null", object);
            //throw EXPF.exception(ErrorCode.JsonSerializer,true);
            throw e;
        }
    }

    public String toString(Object object,PropertyFilterInfo... filters) throws Exception {
        UnifyFilterProvider provider = createProvider(filters);
        try {
            return OBJECT_MAPPER.writer(provider).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warnf(e,"Convert [{} {}] to string failed",object != null ? object.getClass() : "null", object);
            //throw EXPF.exception(ErrorCode.JsonSerializer,true);
            throw e;
        }
    }

    public <T> T toObject(String json,Class<T> objectClass) throws Exception {
        JavaType javaType = TYPE_FACTORY.constructType(objectClass);
        try {
            return OBJECT_MAPPER.readValue(json,javaType);
        } catch (IOException e) {
            log.warnf(e,"Convert [{}] to object [{}] failed", json, objectClass);
            //throw EXPF.exception(ErrorCode.JsonDeserializer,true);
            throw e;
        }
    }

    public <T> T toArray(String json,Class<T> objectClass) throws Exception {
        ArrayType arrayType = TYPE_FACTORY.constructArrayType(objectClass);
        try {
            return OBJECT_MAPPER.readValue(json,arrayType);
        } catch (IOException e) {
            log.warnf(e,"Convert [{}] to object [{}] failed", json, objectClass);
            //throw EXPF.exception(ErrorCode.JsonDeserializer,true);
            throw e;
        }
    }

    public <E,C extends Collection<E>> C toCollection(String json, Class<C> collectionClass, Class<E> elementClass) throws Exception {
        CollectionLikeType collectionLikeType = TYPE_FACTORY.constructCollectionLikeType(collectionClass, elementClass);
        try {
            return OBJECT_MAPPER.readValue(json,collectionLikeType);
        } catch (IOException e) {
            log.warnf(e,"Convert [{}] to collection [{}<{}>] failed", json, collectionClass, elementClass);
            //throw EXPF.exception(ErrorCode.JsonDeserializer,true);
            throw e;
        }
    }

    public <E> ArrayList<E> toArrayList(String json, Class<E> elementClass) throws Exception {
        return toCollection(json, ArrayList.class, elementClass);
    }

    public <E> HashSet<E> toHashSet(String json, Class<E> elementClass) throws Exception {
        return toCollection(json, HashSet.class, elementClass);
    }

    public <K,V,M extends Map<K,V>> M toMap(String json, Class<M> mapClass, Class<K> keyClass, Class<V> valueClass) throws Exception {
        MapType mapType = TYPE_FACTORY.constructMapType(mapClass, keyClass, valueClass);
        try {
            return OBJECT_MAPPER.readValue(json,mapType);
        } catch (IOException e) {
            log.warnf(e,"Convert [{}] to map [{}<{}, {}>] failed", json, mapClass, keyClass, valueClass);
            //FIXME 重构
            //throw EXPF.exception(ErrorCode.JsonDeserializer,true);
            throw e;
        }
    }

    public <K, V> HashMap<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) throws Exception {
        return toMap(json, HashMap.class, keyClass, valueClass);
    }

    public <K, V> HashMap<K, V> toMap(String json, Class<V> valueClass) throws Exception {
        return toMap(json, HashMap.class, String.class, valueClass);
    }

    public <K, V> HashMap<K, V> toMap(String json) throws Exception {
        return toMap(json, HashMap.class, String.class, Object.class);
    }

    public UnifyFilterProvider createProvider(PropertyFilterInfo[] filters) {
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
                provider.addFilter(filterInfo.get_clazz(), SimpleBeanPropertyFilter.filterOutAllExcept(addAll(filterInfo.get_properties(), includeNames)));
            } else {
                provider.addFilter(filterInfo.get_clazz(), SimpleBeanPropertyFilter.serializeAllExcept(addAll(addAll(filterInfo.get_properties(), excludeNames), DEFAULT_EXCLUDE_PROPERTIES)));
            }

        }

        PropertyFilter defaultFilter;
        // 如果包含全局包含字段，直接设置默认provider为FilterOut
        if (!CollectionUtils.isEmpty(include)) {
            defaultFilter = SimpleBeanPropertyFilter.filterOutAllExcept(include.toArray(new String[include.size()]));
        } else {
            String[] strings = addAll(exclude.toArray(new String[exclude.size()]), DEFAULT_EXCLUDE_PROPERTIES);
            defaultFilter = SimpleBeanPropertyFilter.serializeAllExcept(strings);
        }

        provider.setDefaultFilter(defaultFilter);
        return provider;
    }


    public <T> T[] addAll(final T[] array1, final T... array2) {
        if (array1 == null) {
            return null == array2 ? null : array2.clone();
        } else if (array2 == null) {
            return null == array1 ? null : array1.clone();
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (final ArrayStoreException ase) {
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
            }
            throw ase;
        }
        return joinedArray;
    }


    private static class FloatSerializer extends com.fasterxml.jackson.databind.ser.std.StdSerializer<Float> {

        // fields
        private static final DecimalFormat formatter = new DecimalFormat();

        /**
         *
         */
        public FloatSerializer() {
            super(Float.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(Float value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (Float.isNaN(value) || Float.isInfinite(value)) {
                generator.writeNumber(0);
            } else {
                generator.writeRawValue(formatter.format(value).replace(",", ""));
            }
        }
    }

    private static class DoubleSerializer extends com.fasterxml.jackson.databind.ser.std.StdSerializer<Double> {

        // fields
        private static final DecimalFormat formatter = new DecimalFormat();

        /**
         *
         */
        public DoubleSerializer() {
            super(Double.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(Double value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                generator.writeNumber(0);
            } else {
                generator.writeRawValue(formatter.format(value).replace(",", ""));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
            return createSchemaNode("number", true);
        }
    }
}
