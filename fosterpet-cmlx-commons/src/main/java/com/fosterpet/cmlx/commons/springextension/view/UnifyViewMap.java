package com.fosterpet.cmlx.commons.springextension.view;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc jackson 过滤器使用
 * UnifyView中，默认code,data等字段，使用此Map包含
 * @Author cmlx
 * @Date 2019-12-3 0003 16:35
 */
public class UnifyViewMap<K, V> extends HashMap<K, V> {

    public UnifyViewMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public UnifyViewMap(int initialCapacity) {
        super(initialCapacity);
    }

    public UnifyViewMap() {
        super();
    }

    public UnifyViewMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

}

