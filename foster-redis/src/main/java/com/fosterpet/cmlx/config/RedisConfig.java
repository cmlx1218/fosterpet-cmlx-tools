package com.fosterpet.cmlx.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-10-30 0030 16:55
 */
@Slf4j
@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisCacheProperties.class)
public class RedisConfig {





}
