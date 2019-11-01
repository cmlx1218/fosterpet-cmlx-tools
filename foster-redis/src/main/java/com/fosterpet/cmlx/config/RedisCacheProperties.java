package com.fosterpet.cmlx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-10-30 0030 16:57
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisCacheProperties {
}
