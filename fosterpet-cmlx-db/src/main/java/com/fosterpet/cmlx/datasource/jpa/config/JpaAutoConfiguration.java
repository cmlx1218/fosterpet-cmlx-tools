package com.fosterpet.cmlx.datasource.jpa.config;

import com.fosterpet.cmlx.datasource.jdbc.config.JdbcAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc JPA配置
 * @Author cmlx
 * @Date 2019-12-3 0003 18:42
 */
@Configuration
@EnableConfigurationProperties(JpaProperties.class)
/**
 *  控制Configuration是否生效
 * name和value只能存在一个，用来从application.properties中读取某个属性值
 * 属性值如果为空，返回false，不为空与havingValue指定的值进行比较，如果一样就返回true，否则返回false
 * 如果返回false，则该configuration不生效，为true生效
 */
@ConditionalOnProperty(value = "cmlx.jpa.enable", havingValue = "true")
/**
 * 加载配置的类之后再加载当前类
 */
@AutoConfigureAfter({JdbcAutoConfiguration.class})
/**
 *  repositoryBaseClass:在特定环境中指定生成repository的所用的代理基类
 */
@EnableJpaRepositories(basePackages = {"com.fosterpet.cmlx.persist.repository"}, repositoryBaseClass = JpaRepositoryFactoryBean.class)
public class JpaAutoConfiguration {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean managerFactoryBean(EntityManagerFactoryBuilder builder, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManager = builder.dataSource(dataSource).packages("com.aimymusic.appserver.persist.entity").persistenceUnit("entityManager").build();
        entityManager.setJpaVendorAdapter(jpaVendorAdapter());
        Map<String, String> map = new HashMap<>();
        map.put("javax.persistence.validation.mode", "none");
        entityManager.setJpaPropertyMap(map);
        return entityManager;
    }

    @Bean(name = "transactionManager")
    @ConditionalOnProperty(value = "appserver.jpa.enable-transaction", havingValue = "true")
    public PlatformTransactionManager businessTransactionManager(LocalContainerEntityManagerFactoryBean managerFactoryBean) {
        return new JpaTransactionManager(managerFactoryBean.getObject());
    }

}
