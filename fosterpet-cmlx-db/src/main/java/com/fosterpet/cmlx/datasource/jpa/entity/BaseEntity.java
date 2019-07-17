package com.fosterpet.cmlx.datasource.jpa.entity;

import com.fosterpet.cmlx.commons.constant.Constant;
import lombok.Data;

/**
 * 基础Entity
 * @Author cmlx
 * @Date 2019-7-17 0017 10:37
 * @Version 1.0
 */
@Data
public abstract class BaseEntity {

    protected Integer dataState = Constant.DataState.Available.ordinal();
    protected Long createTime;
    protected Long updateTime;

}
