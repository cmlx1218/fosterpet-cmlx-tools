package com.fosterpet.cmlx.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.PropertyDescriptor;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-5 0005 14:52
 */
@Data
@AllArgsConstructor
public class EntityPropertyInfo {

    private PropertyDescriptor primaryKey;
    private PropertyDescriptor[] otherKey;

}
