/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * 删除请求
 *
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 */
public class DelRequest<V extends BaseTypeField> extends CommonRequest {
    /**
     * model id
     */
    private Long id;
    /**
     * 查询条件
     */
    private ConditionPair<V>[] conditions;
    /**
     * 返回字段
     */
    private V[] resFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public V[] getResFields() {
        return resFields;
    }

    public void setResFields(V[] resFields) {
        this.resFields = resFields;
    }

	public ConditionPair<V>[] getConditions() {
		return conditions;
	}

	public void setConditions(ConditionPair<V>[] conditions) {
		this.conditions = conditions;
	}

}
