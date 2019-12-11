package com.fosterpet.cmlx.datasource.jpa.config;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * @Desc JPA Repository 工厂
 *
 * @Author cmlx
 * @Date 2019-12-6 0006 11:35
 */
public class RepositoryFactory extends JpaRepositoryFactory {
    public RepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleRepositoryImpl.class;
    }
}
