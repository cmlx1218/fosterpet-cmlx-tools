package com.fosterpet.cmlx.datasource.aop;

import com.fosterpet.cmlx.datasource.jdbc.annotation.DataSource;
import com.fosterpet.cmlx.datasource.jdbc.multiData.DynamicDataSourceRouter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-5 0005 17:12
 */
@Aspect
@Order(3) //执行顺序
public class DynamicDataSourceAspect {
    @Autowired
    private DynamicDataSourceRouter dataSourceRouter;

    @Around("@annotation(dataSource)")
    public Object around(ProceedingJoinPoint pjp, DataSource dataSource) throws Throwable {
        dataSourceRouter.setDataSource(dataSource.key());
        try {
            return pjp.proceed();
        } finally {
            dataSourceRouter.clearDataSource();
        }
    }
}
