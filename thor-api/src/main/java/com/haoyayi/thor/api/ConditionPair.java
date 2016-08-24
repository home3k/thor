/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @title
 * @description API条件查询Pair.  field -> func -> value...
 */
public class ConditionPair<T extends ConditionField> implements Cloneable {

    /**
     * 条件操作类型. 比如in,>=之类
     */
    private ConditionFunc func;
    /**
     * condition左值field.
     */
    private T field;
    /**
     * condition右值value
     */
    private Object[] value;

    public ConditionPair(ConditionFunc func, T field, Object... value) {
        this.func = func;
        this.field = field;
        this.value = value;
    }

    public ConditionPair(){}
    public ConditionFunc getFunc() {
        return func;
    }

    public void setFunc(ConditionFunc func) {
        this.func = func;
    }

    public Object[] getValue() {
        return value;
    }

    public void setValue(Object[] value) {
        this.value = value;
    }

    public T getField() {
        return field;
    }

    public void setField(T field) {
        this.field = field;
    }

	@Override
	public ConditionPair<ConditionField> clone() {
		try {
			return (ConditionPair<ConditionField>) super.clone();
		} catch (Exception e) {
			throw new RuntimeException("ConditionPair clone error.", e);
		}
	}
}
