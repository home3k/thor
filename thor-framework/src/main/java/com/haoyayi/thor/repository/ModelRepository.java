/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.repository;

import java.util.Map;
import java.util.Set;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.model.ModelPair;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public interface ModelRepository<T extends BaseType, R extends BaseTypeField> {

    /**
     * 根据主键id获取物料对象
     *
     * @param ids
     * @return
     */
    Map<Long, T> getModelById(Set<Long> ids);

    /**
     * @param ids
     * @param fields
     * @return
     */
    Map<Long, T> getModelById(Set<Long> ids, Set<R> fields);


    /**
     * 根据Model主键，及关联field查询Model相关的field
     *
     * @param ids
     * @param fields
     * @return
     */
    Map<Long, Map<R, Object>> getModelField(Set<Long> ids, Set<R> fields);
    
    /**
     * 根据条件查询
     * @param optid
     * @param conditions
     * @param fields
     * @return
     */
    Map<Long, T> getModelByCondition(Long optid, Map<R, Object> conditions, Set<R> fields);

    /**
     * 根据主键id删除物料对象
     *
     * @param id2model
     * @return
     */
    void delModelById(Long opuid, Map<Long, T> id2model);


    /**
     * 添加对象
     *
     * @param optid
     * @param id2model
     * @return
     */
    Map<Long, T> addModel(Long optid, Map<Long, T> id2model);

    /**
     * 修改对象
     *
     * @param optid
     * @param models
     * @return
     */
    Map<Long, T> saveModel(Long optid, Map<Long, ModelPair<T, R>> models);

    /**
     * field填充为T
     *
     * @param modelFields
     * @return
     */
    Map<Long, T> fillModel(Map<Long, Map<R, Object>> modelFields);
}

