/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;


import java.util.Map;

/**
 * 添加请求
 *
 * @param <V>
 * @author home3k (sunkai@51haoyayi.com)
 */
public class AddRequest<V extends BaseTypeField> extends CommonRequest {

    /**
     * 添加字段
     */
    private Map<V, Object> fields;

    public Map<V, Object> getFields() {
        return fields;
    }

    public void setFields(Map<V, Object> fields) {
        this.fields = fields;
    }
}
