package com.fosterpet.cmlx.commons.model;

import lombok.Data;

/**
 * @Desc 过滤信息，当calss = null，将设置为全局过滤
 * @Author cmlx
 * @Date 2019-12-3 0003 16:30
 */
@Data
public class PropertyFilterInfo {
    private Class<?> _clazz;
    private boolean _include;
    private String[] _properties;

    public PropertyFilterInfo(String... properties) {
        this(true, properties);
    }

    public PropertyFilterInfo(boolean include, String... properties) {
        this(include, null, null == properties ? new String[0] : properties);
    }

    public PropertyFilterInfo(boolean include, Class<?> clazz, String... properties) {
        this._clazz = clazz;
        this._include = include;
        this._properties = null == properties ? new String[0] : properties;
    }
}
