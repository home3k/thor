/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 * @description
 */
public class ModRequest<V extends BaseTypeField> extends CommonRequest {
    /**
     * 修改id
     */
    private Long id;
    /**
     * 修改字段
     */
    private Map<V, Object> fields;
    /**
     * 返回字段
     */
    private V[] resFields;

    public Map<V, Object> getFields() {
        return fields;
    }

    public void setFields(Map<V, Object> fields) {
        this.fields = fields;
    }

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
}
