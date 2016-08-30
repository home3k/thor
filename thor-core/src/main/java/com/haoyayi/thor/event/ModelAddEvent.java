/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.event;

import java.util.Map;

import com.haoyayi.thor.api.BaseType;


/**
 * 添加Model事件
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModelAddEvent<T extends BaseType> extends AbstractModelEvent {

    private Map<Long, T> newModels;

    private String modelType;

    @Override
    public String getModelType() {
        return this.modelType;
    }

    public ModelAddEvent(Object source) {
        super(source);
    }

    public ModelAddEvent(Map<Long, T> newModels,
                         String modelType) {
        super(newModels);
        this.newModels = newModels;
        this.modelType = modelType;
    }

    public Map<Long, T> getNewModels() {
        return newModels;
    }

    public void setNewModels(Map<Long, T> newModels) {
        this.newModels = newModels;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
