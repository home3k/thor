/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class QueryGroupByRequest<C extends ConditionField, F extends BaseTypeField> extends CommonRequest {

    /**
     * 查询条件
     */
    private ConditionPair<C>[] conditions;

    /**
     * group by的字段
     */
    private Set<F> groupByFields;

    /**
     * group by 函数字段
     */
    private Map<GroupFunc, F> groupFuncMap;

    public ConditionPair<C>[] getConditions() {
        return conditions;
    }

    public void setConditions(ConditionPair<C>[] conditions) {
        this.conditions = conditions;
    }

    public Set<F> getGroupByFields() {
        return groupByFields;
    }

    public void setGroupByFields(Set<F> groupByFields) {
        this.groupByFields = groupByFields;
    }

    public Map<GroupFunc, F> getGroupFuncMap() {
        return groupFuncMap;
    }

    public void setGroupFuncMap(Map<GroupFunc, F> groupFuncMap) {
        this.groupFuncMap = groupFuncMap;
    }
}
