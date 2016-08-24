/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.dal.impl;

import com.haoyayi.thor.api.GroupFunc;
import com.haoyayi.thor.dal.*;
import com.haoyayi.thor.dal.base.*;
import com.haoyayi.thor.dal.bo.*;
import com.haoyayi.thor.dal.meta.*;
import com.haoyayi.thor.dal.rowmapper.*;
import com.haoyayi.thor.dal.columnrowmapper.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.haoyayi.thor.dal.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Pair;
import com.haoyayi.thor.constants.ModelConstants;

/**
* This class is automatically generated, Unless special scene, DO NOT modify!!
*/
@Service
<#if dict >
public class ${Classname}DAOImpl extends AbstractDictDao<${Classname},${Classname}Column> implements ${Classname}DAO {
<#else>
public class ${Classname}DAOImpl extends AbstractDAO<${Classname},${Classname}Column> implements ${Classname}DAO {
</#if>

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    protected ${Classname}Column[] getAllColumns() {
        return ${Classname}Column.values();
    }

    @Override
    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getTablename() {
        return "${database}.${tablename}";
    }

    @Override
    protected RowMapper<${Classname}> getRowMapper() {
        return new ${Classname}RowMapper();
    }

    @Override
    protected RowMapper<Map<${Classname}Column, Object>> getRowMapper(Set<${Classname}Column> fields) {
        return new ${Classname}ColumnRowMapper(fields);
    }

    @Override
    protected ${Classname}Column getPk() {
        return ${Classname}Column.${pk};
    }

<#if dict >
<#else>
    @Override
    public void afterPropertiesSet() throws Exception {
    }
</#if>

    @Override
    public List<Long> add${Classname}(List<${Classname}> ${classname}) {
        return add(${classname});
    }

    @Override
    public void mod${Classname}(Map<Long, Map<${Classname}Column, Object>> id2items) {
        this.modByRow(id2items);
    }

    @Override
    public void mod${Classname}(Set<Long> ids, Map<${Classname}Column, Object> items) {
        this.modByColumn(ids, items);
    }

    @Override
    public void mod${Classname}(Map<${Classname}Column, Object> conditions, Map<${Classname}Column, Object> items){
        super.mod(conditions,items);
    }

    @Override
    public void del${Classname}(Set<Long> ids){
        if (ids==null || ids.size()==0) {
            return;
        }
        Map<${Classname}Column, Object> items = new HashMap<${Classname}Column, Object>();

        <#if del>
        items.put(${Classname}Column.${isdel}, ModelConstants.IS_DEL);
        mod${Classname}(ids, items);
        <#else>
        items.put(${Classname}Column.${pk}, ids);
        del(items);
        </#if>

    }

    @Override
    public Map<Long, ${Classname}> get${Classname}(Set<Long> ids) {
        Map<Long, ${Classname}> result = new HashMap<Long, ${Classname}>();
        if (ids==null || ids.size()==0) {
            return result;
        }
        Map<${Classname}Column, Object> items = new HashMap<${Classname}Column, Object>();
        items.put(${Classname}Column.${pk}, ids);
        <#if del>
        items.put(${Classname}Column.${isdel}, ModelConstants.IS_NOT_DEL);
        </#if>
        return getBoByBasicCondition(items);
    }

    @Override
    public Map<Long, Map<${Classname}Column, Object>> get${Classname}(Set<Long> ids, Set<${Classname}Column> fields) {
        Map<${Classname}Column, Object> conditions = new HashMap<${Classname}Column, Object>();
        conditions.put(${Classname}Column.${pk}, ids);
        <#if del>
        conditions.put(${Classname}Column.${isdel}, ModelConstants.IS_NOT_DEL);
        </#if>
        return super.getItemsByCondition(conditions, fields);
    }

    @Override
    public Map<Long, Map<${Classname}Column, Object>> get${Classname}ByCondition(Map<${Classname}Column, Object> conditions, Set<${Classname}Column> fields,
        List<Pair<${Classname}Column, Boolean>> orderbys, Integer num, Integer offset) {
        <#if del>
        if(!conditions.containsKey(${Classname}Column.${isdel})) {
            conditions.put(${Classname}Column.${isdel}, ModelConstants.IS_NOT_DEL);
        }
        </#if>
        return super.getItemsByCondition(conditions, fields, orderbys, num, offset);
    }

    @Override
    public void del${Classname}ByCondition(Map<${Classname}Column, Object> condition) {
        if(condition==null || condition.size()==0) {
            return;
        }
        <#if del>
        Map<${Classname}Column, Object> items = new HashMap<${Classname}Column, Object>();
        items.put(${Classname}Column.${isdel}, ModelConstants.IS_DEL);
        mod${Classname}(condition, items);
        <#else>
        del(condition);
        </#if>
    }

    @Override
    public Map<Long, ${Classname}> get${Classname}(Map<${Classname}Column, Object> conditions) {
        return super.getBoByBasicCondition(conditions);
    }

    @Override
    public List<Long> add${Classname}AutoKey(List<${Classname}> items) {
        return add(items, true);
    }

    @Override
    public Map<Long, Map<${Classname}Column, Object>> get${Classname}(Map<${Classname}Column, Object> conditions, Set<${Classname}Column> fields) {
        return super.getItemsByCondition(conditions, fields);
    }

    @Override
    public  Long getCountByCondition(Map<${Classname}Column, Object> conditions) {
        return super.getCountByCondition(conditions);
    }
    
<#if dict >
    protected ${Classname}Column getDictColumn() {
        return ${Classname}Column.${dictField};
    }
</#if>
    
    @Override
    public List<Map<String, Object>> get${Classname}GroupResultByCondition(Map<${Classname}Column, Object> conditions, Set<${Classname}Column> groupByFields, Map<GroupFunc, ${Classname}Column> groupFuncMap) {
        return super.getGroupResultByCondition(conditions, groupByFields, groupFuncMap);
    }

}
