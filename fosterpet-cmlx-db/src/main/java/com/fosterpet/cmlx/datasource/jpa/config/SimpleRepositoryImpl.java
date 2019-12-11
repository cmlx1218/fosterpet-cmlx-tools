package com.fosterpet.cmlx.datasource.jpa.config;

import com.fosterpet.cmlx.commons.constant.Constant;
import com.fosterpet.cmlx.commons.model.EntityPropertyInfo;
import com.fosterpet.cmlx.datasource.jpa.entity.BaseEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Desc JPA 基础Repository实现类
 * @Author cmlx
 * @Date 2019-12-6 0006 11:37
 */
public class SimpleRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements SimpleRepository<T, ID> {

    protected final EntityManager em;

    protected final JpaEntityInformation information;

    protected final Session session;

    public SimpleRepositoryImpl(Class domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.information = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
        this.session = (Session) em.getDelegate();
    }

    public SimpleRepositoryImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
        this.information = entityInformation;
        this.session = (Session) entityManager.getDelegate();
    }


    @Override
    public <S extends T> S save(S entity) {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }
        return super.save(entity);
    }

    @Override
    public <S extends T> S saveEntity(S entity) {
        return super.save(entity);
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }
        return super.saveAndFlush(entity);
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        ArrayList result = new ArrayList();
        if (entities == null) {
            return result;
        } else {
            Iterator<S> var3 = entities.iterator();
            while (var3.hasNext()) {
                S entity = var3.next();
                result.add(this.save(entity));
            }
            return result;
        }
    }

    @Override
    public CriteriaBuilderImpl extBuilder() {
        return (CriteriaBuilderImpl) em.getCriteriaBuilder();
    }

    @Override
    public <D> List<D> extExecuteCQ(CriteriaQuery<D> cq) {
        return em.createQuery(cq).getResultList();
    }

    @Override
    public <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, Pageable pageable) {
        TypedQuery<D> query = em.createQuery(cq);
        if (null != pageable) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    @Override
    public <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, int page, int size) {
        TypedQuery<D> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(page);
        return query.getResultList();
    }

    @Override
    public <D> int extExcuteCU(CriteriaUpdate<D> cu) {
        return em.createQuery(cu).executeUpdate();
    }

    @Override
    public <D> int extExcuteCD(CriteriaDelete<D> cd) {
        return em.createQuery(cd).executeUpdate();
    }

    @Override
    public <S extends T> S extSaveFull(S entity) throws Exception {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }
        if (information.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            int i = this.extUpdateFull(entity);
            if (i == 0) {
                throw EXPF.E404(this.getClass().getSimpleName(), true);
            }
            return entity;
        }
    }

    @Override
    public <S extends T> S extSaveNotNull(S entity) throws Exception {
        if (entity instanceof BaseEntity) {
            fillTime(entity);
        }
        if (information.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            int i = this.extUpdateNotNull(entity);
            if (i == 0) {
                throw EXPF.E404(this.getClass().getSimpleName(), true);
            }
            return entity;
        }
    }

    @Override
    public int extDeleteBySoft(ID id) {
        CriteriaBuilderImpl builder = this.extBuilder();
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        Root<T> from = cu.from(getDomainClass());
        cu.set("dataState", Constant.DataState.Invalid.ordinal());
        cu.set("updateTime", System.currentTimeMillis());
        EntityPropertyInfo property = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        Predicate where = builder.equal(from.get(property.getPrimaryKey().getDisplayName()), id);
        cu.where(where);
        return this.extExcuteCU(cu);
    }

    @Override
    public int extDeleteByPhysically(ID id) {
        CriteriaBuilderImpl builder = this.extBuilder();
        CriteriaDelete<T> cd = builder.createCriteriaDelete(getDomainClass());
        Root<T> from = cd.from(getDomainClass());
        EntityPropertyInfo property = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        Predicate where = builder.equal(from.get(property.getPrimaryKey().getDisplayName()), id);
        cd.where(where);
        return this.extExcuteCD(cd);
    }

    @Override
    public int extDisable(ID id) {
        CriteriaBuilderImpl builder = this.extBuilder();
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        Root<T> from = cu.from(getDomainClass());
        cu.set("dataState", Constant.DataState.Disable.ordinal());
        cu.set("updateTime", System.currentTimeMillis());
        EntityPropertyInfo property = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        Predicate where = builder.equal(from.get(property.getPrimaryKey().getDisplayName()), id);
        cu.where(where);
        return this.extExcuteCU(cu);
    }

    @Override
    public int extAvaliable(ID id) {
        CriteriaBuilderImpl builder = this.extBuilder();
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        Root<T> from = cu.from(getDomainClass());
        cu.set("dataState", Constant.DataState.Available.ordinal());
        cu.set("updateTime", System.currentTimeMillis());
        EntityPropertyInfo property = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        Predicate where = builder.equal(from.get(property.getPrimaryKey().getDisplayName()), id);
        cu.where(where);
        return this.extExcuteCU(cu);
    }

    @Override
    public int extRefreshUpdateTime(ID id, Long time) {
        CriteriaBuilderImpl builder = this.extBuilder();
        CriteriaUpdate<T> cu = builder.createCriteriaUpdate(getDomainClass());
        Root<T> from = cu.from(getDomainClass());
        cu.set("updateTime", time);
        EntityPropertyInfo property = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        Predicate where = builder.equal(from.get(property.getPrimaryKey().getDisplayName()), id);
        cu.where(where);
        return this.extExcuteCU(cu);
    }


    public int extRefreshUpdateTime(ID id, String time) {
        return extRefreshUpdateTime(id, Long.valueOf(time));
    }

    @Override
    public <S extends T> int extUpdateFull(S entity) throws InvocationTargetException, IllegalAccessException {
        if (entity instanceof BaseEntity) {
            this.fillTime(entity);
        }
        Class<S> clazz = (Class<S>) entity.getClass();
        CriteriaBuilder builder = this.extBuilder();
        CriteriaUpdate<S> cu = builder.createCriteriaUpdate(clazz);
        Root<S> from = cu.from(clazz);
        EntityPropertyInfo proDes = JpaEntityPropertyUtility.getProperty(clazz, true);
        Assert.notNull(proDes, "PropertyDescriptor is not null");
        PropertyDescriptor[] otherKey = proDes.getOtherKey();
        for (PropertyDescriptor pro : otherKey) {
            Method readMethod = pro.getReadMethod();
            Object invoke = readMethod.invoke(entity);
            cu.set(pro.getName(), invoke);
        }
        PropertyDescriptor primaryKey = proDes.getPrimaryKey();
        Method readMethod = primaryKey.getReadMethod();
        Object invoke = readMethod.invoke(entity);
        Assert.notNull(invoke, "Modify the object [id] is empty");
        Predicate primary = builder.equal(from.get(primaryKey.getName()), invoke);
        cu.where(primary);
        return this.extExcuteCU(cu);
    }

    @Override
    public <S extends T> int extUpdateNotNull(S entity) throws InvocationTargetException, IllegalAccessException {
        if (entity instanceof BaseEntity) {
            this.fillTime(entity);
        }
        Class<S> clazz = (Class<S>) entity.getClass();
        CriteriaBuilder builder = this.extBuilder();
        CriteriaUpdate<S> cu = builder.createCriteriaUpdate(clazz);
        Root<S> from = cu.from(clazz);
        EntityPropertyInfo proDes = JpaEntityPropertyUtility.getProperty(clazz, false);
        Assert.notNull(proDes, "PropertyDescriptor is not null");
        PropertyDescriptor[] otherKey = proDes.getOtherKey();
        for (PropertyDescriptor pro : otherKey) {
            Method readMethod = pro.getReadMethod();
            Object invoke = readMethod.invoke(entity);
            if (null != invoke) {
                cu.set(pro.getName(), invoke);
            }
        }
        PropertyDescriptor primaryKey = proDes.getPrimaryKey();
        Method readMethod = primaryKey.getReadMethod();
        Object invoke = readMethod.invoke(entity);
        Assert.notNull(invoke, "Modify the object [id] is not null");
        Predicate primary = builder.equal(from.get(primaryKey.getName()), invoke);
        cu.where(primary);
        return this.extExcuteCU(cu);
    }

    @Transactional(readOnly = true)
    @Override
    public T extFindOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public T extFindOne(ID id, Constant.DataState state) {
        CriteriaBuilderImpl builder = this.extBuilder();
        EntityPropertyInfo property = JpaEntityPropertyUtility.getProperty(getDomainClass(), true);
        CriteriaQuery<T> query = builder.createQuery(getDomainClass());
        Root<T> from = query.from(getDomainClass());
        Predicate dataState = builder.equal(from.get("dataState"), state.ordinal());
        Predicate id1 = builder.equal(from.get(property.getPrimaryKey().getName()), id);
        query.where(id1, dataState);
        List<T> ts = this.extExecuteCQ(query);
        return CollectionUtils.isEmpty(ts) ? null : ts.get(0);
    }

    @Transactional(readOnly = true)
    @Override
    public <D> List<D> extFindByNativeSQL(String sql, Class<D> clazz, List<Object> parameter) {
        Assert.notNull(sql, "NativeSql is not null");
        SQLQuery cq = session.createSQLQuery(sql);
        if (null != clazz)
            addSclar(cq, clazz);
        int i = 0;
        for (Object obj : parameter) {
            cq.setParameter(i++, obj);
        }
        return cq.list();
    }

    private void fillTime(T entity) {
        Long time = System.currentTimeMillis();
        BaseEntity baseEntity = (BaseEntity) entity;
        if (baseEntity.getCreateTime() == null) {
            baseEntity.setCreateTime(time);
        }
        baseEntity.setUpdateTime(time);
    }

    public static void addSclar(SQLQuery query, Class<?> clazz) {
        Assert.notNull(query, "Query sql required");
        Assert.notNull(clazz, "Entity class required");
        PropertyDescriptor[] propertyDescriptor = JpaEntityPropertyUtility.getEntityPropertyDescriptor(clazz);
        for (PropertyDescriptor descriptor : propertyDescriptor) {
            String name = descriptor.getName();
            Class<?> propertyType = descriptor.getPropertyType();
            if (propertyType == long.class || propertyType == Long.class) {
                query.addScalar(name, LongType.INSTANCE);
            } else if (propertyType == int.class || propertyType == Integer.class) {
                query.addScalar(name, IntegerType.INSTANCE);
            } else if ((propertyType == char.class) || (propertyType == Character.class)) {
                query.addScalar(name, CharacterType.INSTANCE);
            } else if ((propertyType == short.class) || (propertyType == Short.class)) {
                query.addScalar(name, ShortType.INSTANCE);
            } else if ((propertyType == double.class) || (propertyType == Double.class)) {
                query.addScalar(name, DoubleType.INSTANCE);
            } else if ((propertyType == float.class) || (propertyType == Float.class)) {
                query.addScalar(name, FloatType.INSTANCE);
            } else if ((propertyType == boolean.class) || (propertyType == Boolean.class)) {
                query.addScalar(name, BooleanType.INSTANCE);
            } else if (propertyType == String.class) {
                query.addScalar(name, StringType.INSTANCE);
            } else if (propertyType == java.sql.Date.class) {
                query.addScalar(name, DateType.INSTANCE);
            } else if (propertyType == BigDecimal.class) {
                query.addScalar(name, BigDecimalType.INSTANCE);
            }
        }
        query.setResultTransformer(Transformers.aliasToBean(clazz));
    }
}
