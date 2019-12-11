package com.fosterpet.cmlx.commons.springextension.view;

import com.fosterpet.cmlx.commons.model.PropertyFilterInfo;

/**
 * @Desc 统一处理成功的视图
 * @Author cmlx
 * @Date 2019-12-3 0003 10:29
 */
public class UnifySuccessView extends UnifyView {

    public UnifySuccessView() {
        this(null);
    }

    public UnifySuccessView(Object object) {
        super();
        if(null != object){
            this.addStaticAttribute(DATA,object);
        }
        this.addStaticAttribute(CODE,200);
    }

    public UnifySuccessView(Object object, PropertyFilterInfo... filters) {
        super(filters);
        if(null != object){
            this.addStaticAttribute(DATA,object);
        }
        this.addStaticAttribute(CODE,200);
    }

}
