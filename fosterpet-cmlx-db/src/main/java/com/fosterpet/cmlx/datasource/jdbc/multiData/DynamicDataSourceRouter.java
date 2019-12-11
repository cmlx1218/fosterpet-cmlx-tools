package com.fosterpet.cmlx.datasource.jdbc.multiData;

import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Desc 多数据源路由
 * @Author cmlx
 * @Date 2019-12-5 0005 17:10
 */
public class DynamicDataSourceRouter extends AbstractRoutingDataSource {
    private static final Log log = LoggerFactory.make();

    private static final ThreadLocal<String> dataSources = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    public String getDataSource() {
        return dataSources.get();
    }

    public void clearDataSource() {
        dataSources.remove();
    }

    public void setDataSource(String customerType) {
        dataSources.set(customerType);
    }

}
