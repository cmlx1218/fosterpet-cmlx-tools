package com.fosterpet.cmlx.datasource.jdbc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-5 0005 10:49
 */
@Data
/**
 * 获取application.yml/properties中的参数值
 */
@ConfigurationProperties(prefix = "cmlx.jdbc")
public class JdbcProperties {
    private boolean enableLazyProxy = false;
    private boolean enableDynamicSwitching = false;

    private DataSourceProperties defaultDataSource = new DataSourceProperties();
    private List<DataSourceProperties> dataSource = new ArrayList<>();
}
