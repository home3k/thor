/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.repository;

import com.haoyayi.thor.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface ModelConditionQueryRepository<T extends BaseType, V extends BaseTypeField, C extends ConditionField> {

    Map<Long, Map<V, Object>> getModelByCondition(List<ConditionPair<C>> conditions, Option[] options, Set<V> fields);

    Map<Long, T> getModelByCondition(Map<V, Object> conditions, Set<V> fields);

    Long getModelCountByCondition(Map<V, Object> conditions);

    List<Map<String, Object>> getModelGroupByByCondition(Map<V, Object> conditions, Set<V> groupByFields, Map<GroupFunc, V> groupFuncMap);

}
