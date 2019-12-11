package com.fosterpet.cmlx.datasource.jdbc.config;

import com.fosterpet.cmlx.commons.support.EntityPropertyUtility;
import com.fosterpet.cmlx.commons.support.ReflectionUtility;
import com.fosterpet.cmlx.datasource.aop.DynamicDataSourceAspect;
import com.fosterpet.cmlx.datasource.jdbc.multiData.DynamicDataSourceRouter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-5 0005 10:42
 */
@Configuration
/**
 * 使使用@ConfigurationProperties注解的类生效
 */
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcAutoConfiguration implements ApplicationContextAware, InitializingBean {

    private final JdbcProperties jdbcProperties;
    private GenericApplicationContext applicationContext;

    public JdbcAutoConfiguration(JdbcProperties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        registerDataSource(jdbcProperties.getDefaultDataSource());
        jdbcProperties.getDataSource().forEach(this::registerDataSource);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof GenericApplicationContext) {
            this.applicationContext = (GenericApplicationContext) applicationContext;
        }
    }

    private String makeDataSourceBeanName(String name) {
        return "aimymusic_appserver_datasource_" + name.replace("-", "_").replace(".", "_").replace(" ", "_").toLowerCase();
    }

    private void registerDataSource(DataSourceProperties dataSourceProperties) {
        DataSourceProperties defaultSource = new DataSourceProperties();
        EntityPropertyUtility.copyNotNull(jdbcProperties.getDefaultDataSource(),defaultSource);
        EntityPropertyUtility.copyNotNull(dataSourceProperties, defaultSource);
        dataSourceProperties = defaultSource;

        PropertyDescriptor[] propertyDescriptors = EntityPropertyUtility.getPropertyDescriptors(DataSourceProperties.class);
        Map<String, Object> map = new HashMap<>();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            if (dataSourceProperties.ignore().contains(descriptor.getName())) continue;
            Object value = ReflectionUtility.invokeMethod(descriptor.getReadMethod(), dataSourceProperties);
            if (null == value) continue;
            map.put(descriptor.getName(), value);
        }

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        if (jdbcProperties.isEnableLazyProxy()) {
            beanDefinition.setBeanClass(LazyConnectionDataSourceProxy.class);
            DataSource dataSource = DataSourceBuilder.create().build();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                PropertyDescriptor propertyDescriptor = EntityPropertyUtility.getPropertyDescriptor(dataSource.getClass(), entry.getKey());
                ReflectionUtility.invokeMethod(propertyDescriptor.getWriteMethod(), dataSource, entry.getValue());
            }
            beanDefinition.getPropertyValues().addPropertyValue("targetDataSource", dataSource);
        } else {
            beanDefinition.setBeanClass(DataSourceBuilder.create().findType());
            beanDefinition.getPropertyValues().addPropertyValues(map);
        }

        beanDefinition.setSynthetic(true);
        applicationContext.registerBeanDefinition(makeDataSourceBeanName(dataSourceProperties.getName()), beanDefinition);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DynamicDataSourceRouter router = new DynamicDataSourceRouter();
        router.setDataSourceLookup(beanFactoryDataSourceLookup());

        Map<Object, Object> targetDataSources = new LinkedHashMap<>();
        jdbcProperties.getDataSource().forEach(dataSourceProperties -> targetDataSources.put(dataSourceProperties.getName(), makeDataSourceBeanName(dataSourceProperties.getName())));
        router.setTargetDataSources(targetDataSources);

        router.setDefaultTargetDataSource(makeDataSourceBeanName(jdbcProperties.getDefaultDataSource().getName()));
        return router;
    }

    @Bean
    public BeanFactoryDataSourceLookup beanFactoryDataSourceLookup() {
        return new BeanFactoryDataSourceLookup();
    }

    @Bean
    @ConditionalOnProperty(value = "appserver.jdbc.enable-dynamic-switching", havingValue = "true")
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
