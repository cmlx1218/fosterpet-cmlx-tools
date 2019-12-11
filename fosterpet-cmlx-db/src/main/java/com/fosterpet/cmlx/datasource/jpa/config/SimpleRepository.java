package com.fosterpet.cmlx.datasource.jpa.config;

import com.fosterpet.cmlx.commons.constant.Constant;
import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Desc JPA 基础Repository抽象类
 * @Author cmlx
 * @Date 2019-12-6 0006 11:42
 */
//Spring Data Jpa在启动时就不会实例化该类
@NoRepositoryBean
public interface SimpleRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 动态构造查询
     *
     * @return
     */
    CriteriaBuilderImpl extBuilder();

    /**
     * 标准查询
     *
     * @param cq
     * @param <D>
     * @return
     */
    <D> List<D> extExecuteCQ(CriteriaQuery<D> cq);

    <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, Pageable pageable);

    <D> List<D> extExecuteCQ(CriteriaQuery<D> cq, int page, int size);

    /**
     * 标准查询修改
     *
     * @param cu
     * @param <D>
     * @return
     */
    <D> int extExcuteCU(CriteriaUpdate<D> cu);

    /**
     * 标准查询删除
     *
     * @param cd
     * @param <D>
     * @return
     */
    <D> int extExcuteCD(CriteriaDelete<D> cd);

    /**
     * 完整保存，如果有空字段，设置为NULL
     *
     * @param entity
     * @param <S>
     * @return
     * @throws Exception
     */
    <S extends T> S extSaveFull(S entity) throws Exception;

    /**
     * 保存非空字段
     *
     * @param entity
     * @param <S>
     * @return
     * @throws Exception
     */
    <S extends T> S extSaveNotNull(S entity) throws Exception;

    /**
     * 软删除
     *
     * @param id
     * @return
     */
    int extDeleteBySoft(ID id);

    /**
     * 物理删除
     *
     * @param id
     * @return
     */
    int extDeleteByPhysically(ID id);

    /**
     * 禁用
     *
     * @param id
     * @return
     */
    int extDisable(ID id);

    /**
     * 启用
     *
     * @param id
     * @return
     */
    int extAvaliable(ID id);

    /**
     * 刷新时间修改
     *
     * @param id
     * @param time
     * @return
     */
    int extRefreshUpdateTime(ID id, Long time);

    int exRefreshUpdateTime(ID id, String time);

    /**
     * 完整修改，如果有空字段，将设置为NULL
     *
     * @param entity
     * @param <S>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    <S extends T> int extUpdateFull(S entity) throws InvocationTargetException, IllegalAccessException;

    /**
     * 修改非空字段
     *
     * @param entity
     * @param <S>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    <S extends T> int extUpdateNotNull(S entity) throws InvocationTargetException, IllegalAccessException;

    T extFindOne(ID id);

    T extFindOne(ID id, Constant.DataState state);

    /**
     * 原生SQL查询
     *
     * @param sql
     * @param clazz
     * @param parameter
     * @param <D>
     * @return
     */
    <D> List<D> extFindByNativeSQL(String sql, Class<D> clazz, List<Object> parameter);

    <S extends T> S saveEntity(S entity);

}


