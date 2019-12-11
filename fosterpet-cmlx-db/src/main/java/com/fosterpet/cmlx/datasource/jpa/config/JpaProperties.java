package com.fosterpet.cmlx.datasource.jpa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-6 0006 11:30
 */
@Data
@ConfigurationProperties(prefix = "cmlx.jpa")
public class JpaProperties {

    private boolean enabled = false;
    private boolean enableTransaction = false;

}
