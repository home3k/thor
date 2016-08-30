/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.event;

import com.haoyayi.thor.api.BaseType;

import java.util.Map;

/**
 * 删除Model事件
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModelDelEvent<T extends BaseType> extends AbstractModelEvent {

    private Map<Long, T> id2model;

    private String modelType;

    public ModelDelEvent(Object source) {
        super(source);
    }

    public ModelDelEvent(Map<Long, T> id2model, String modelType) {
        super(id2model);
        this.id2model = id2model;
        this.modelType = modelType;
    }

    public Map<Long, T> getId2model() {
        return id2model;
    }

    public void setId2model(Map<Long, T> id2model) {
        this.id2model = id2model;
    }

    @Override
    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
