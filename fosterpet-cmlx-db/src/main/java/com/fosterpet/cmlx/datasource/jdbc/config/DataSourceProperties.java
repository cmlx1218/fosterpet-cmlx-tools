package com.fosterpet.cmlx.datasource.jdbc.config;

import com.fosterpet.cmlx.commons.support.StringUtility;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-5 0005 11:38
 */
@Data
public class DataSourceProperties {

    /**
     * 连接名，用于数据库切换
     */
    private String name;

    /**
     * 库名
     */
    private String db;

    /**
     * 连接地址
     */
    private String baseUrl;

    /**
     * 连接地址参数
     */
    private String searchUrl;

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private Integer maxActive;
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxIdle;
    private Boolean testWhileIdle;
    private Boolean testOnBorrom;
    private Integer minEvictableIdleTimeMillis;
    private Integer timeBetweenEvictionRunsMillis;
    private Integer maxWait;
    private Integer removeAbandonedTimeout;
    private Boolean removeAbandoned;
    private Integer validationInterval;
    private String validationQuery;
    private Boolean defaultAutoCommit;
    private Boolean defaultReadOnly;

    private String makeUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getBaseUrl());

        if (StringUtility.hasText(this.getDb())) {
            builder.append("/").append(this.getDb());
        }

        if (StringUtility.hasText(this.getSearchUrl())) {
            builder.append("?").append(this.getSearchUrl());
        }
        return builder.toString();
    }

    public String getUrl() {
        if (null == url) {
            url = makeUrl();
        }
        return url;
    }

    public List<String> ignore() {
        List<String> list = new ArrayList<>();
        list.add("name");
        list.add("db");
        list.add("baseUrl");
        list.add("searchUrl");
        list.add("class");
        return list;
    }
}
