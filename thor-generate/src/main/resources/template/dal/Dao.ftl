/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.dal;

import com.haoyayi.thor.api.GroupFunc;

import com.haoyayi.thor.dal.bo.*;
import com.haoyayi.thor.dal.meta.*;

import java.util.Map;
import java.util.Set;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import com.haoyayi.thor.dal.base.DictStoreType;

/**
* This class is automatically generated, Unless special scene, DO NOT modify!!
*/
public interface ${Classname}DAO {

<#if dict >
    Map<Long, String> getDict(Map<Long, Long> model4dictId);

    Map<Long, String[]> getDict(Map<Long, Object> model4dictInfos, DictStoreType storeType);
</#if>

    Long getCountByCondition(Map<${Classname}Column, Object> conditions);

    List<Long> add${Classname}(List<${Classname}> ${classname});

    void mod${Classname}(Map<Long,Map<${Classname}Column,Object>> id2items);

    void mod${Classname}(Set<Long> ids,Map<${Classname}Column,Object> items);

    void mod${Classname}(Map<${Classname}Column, Object> conditions, Map<${Classname}Column, Object> items);

    Map<Long, ${Classname}> get${Classname}(Set<Long> ids);

    Map<Long, Map<${Classname}Column, Object>> get${Classname}(Set<Long> ids, Set<${Classname}Column> fields);

    void del${Classname}(Set<Long> ids);

    Map<Long, Map<${Classname}Column, Object>> get${Classname}ByCondition(Map<${Classname}Column, Object> conditions, Set<${Classname}Column> fields,
        List<Pair<${Classname}Column, Boolean>> orderbys, Integer num, Integer offset);

    void del${Classname}ByCondition(Map<${Classname}Column, Object> condition);

    Map<Long, ${Classname}> get${Classname}(Map<${Classname}Column, Object> conditions);

    List<Long> add${Classname}AutoKey(List<${Classname}> items);

    Map<Long, Map<${Classname}Column, Object>> get${Classname}(Map<${Classname}Column, Object> conditions, Set<${Classname}Column> fields);

    List<Map<String, Object>> get${Classname}GroupResultByCondition(Map<${Classname}Column, Object> conditions, Set<${Classname}Column> groupByFields, Map<GroupFunc, ${Classname}Column> groupFuncMap);

}
