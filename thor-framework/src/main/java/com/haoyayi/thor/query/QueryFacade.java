/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.query;

import com.haoyayi.thor.api.*;
import com.haoyayi.thor.common.CheckResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface QueryFacade<T extends BaseType, V extends BaseTypeField, C extends ConditionField> {

    Set<V> addOtherFields(Set<V> fields, String model, Set<String> otherModels, Map<String, String> subModelField4model);

    Map<Long, CheckResult<T>> query(Long optid, ConditionPair<C>[] conditions, Option[] options, Set<String> fields);

    CheckResult<Long> query(Long optid, ConditionPair<C>[] conditions);

    CheckResult<List<Map<String, Object>>> query(Long optid, ConditionPair<C>[] conditions, Set<V> groupByFields, Map<GroupFunc, V> groupFuncMap);

}

