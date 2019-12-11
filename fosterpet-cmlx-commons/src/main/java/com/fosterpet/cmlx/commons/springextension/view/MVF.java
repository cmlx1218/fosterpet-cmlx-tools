package com.fosterpet.cmlx.commons.springextension.view;

import com.fosterpet.cmlx.commons.model.PropertyFilterInfo;
import com.fosterpet.cmlx.commons.support.StringUtility;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 返回值包装面板
 * @Author cmlx
 * @Date 2019-12-3 0003 10:27
 */
public class MVF {

    /**
     * @return
     * @Title: nullData
     * @Description: 返回一个空值
     */
    public static ModelAndView nullData() {
        return new ModelAndView(new UnifySuccessView());
    }

    /**
     * @param entity
     * @param filters
     * @return 返回一个只有一个key-value的值
     */
    public static ModelAndView uniqueKeyData(String key, Object entity, PropertyFilterInfo... filters) {
        if (null == entity) {
            return nullData();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(StringUtils.isEmpty(key) ? "defaultKey" : key, entity);
        return filterData(map, filters);
    }

    /**
     * @param entity
     * @return
     * @Title: idData
     * @Description: 只返回对象的ID
     */
    public static ModelAndView idData(Object entity) {
        if (null == entity) {
            return nullData();
        }
        UnifySuccessView view = new UnifySuccessView(entity, new PropertyFilterInfo(true, "id"));
        return new ModelAndView(view);
    }

    /**
     * 返回子串
     *
     * @param str
     * @return
     */
    public static ModelAndView stringData(String str) {
        if (!StringUtility.hasLength(str)) {
            return nullData();
        }
        return new ModelAndView(new UnifySuccessView(str));
    }

    /**
     * 返回数字
     *
     * @param integer
     * @return
     */
    public static ModelAndView intData(Integer integer) {
        if (null == integer) {
            return nullData();
        }
        return new ModelAndView(new UnifySuccessView(integer));
    }

    /**
     * listId
     *
     * @param entities
     * @return
     */
    public static ModelAndView idListData(List<?> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return nullData();
        }
        Class<?> clazz = entities.get(0).getClass();
        return new ModelAndView(new UnifySuccessView(entities, new PropertyFilterInfo(true, clazz, "id")));
    }

    /**
     * 过滤条件返回
     *
     * @param entities
     * @param filters
     * @return
     */
    public static ModelAndView filterListData(List<?> entities, PropertyFilterInfo... filters) {
        if (CollectionUtils.isEmpty(entities)) {
            return nullData();
        }
        return new ModelAndView(new UnifySuccessView(entities, filters));
    }

    /**
     * 过滤条件返回
     *
     * @param entity
     * @param filters
     * @return
     */
    public static ModelAndView filterData(Object entity, PropertyFilterInfo... filters) {
        if (null == entity) {
            return nullData();
        }

        if (entity instanceof  List) {
            return uniqueKeyData("list", entity, filters);
        }

        return new ModelAndView(new UnifySuccessView(entity, filters));
    }
}
