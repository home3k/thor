/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.event;

import java.util.Map;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.ModelType;


/**
 * 添加Model事件
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModelAddEvent<T extends BaseType> extends AbstractModelEvent {

    private Map<Long, T> newModels;

    private ModelType modelType;

    @Override
    public ModelType getModelType() {
        return this.modelType;
    }

    public ModelAddEvent(Object source) {
        super(source);
    }

    public ModelAddEvent(Long optid, Map<Long, T> newModels,
                         ModelType modelType) {
        super(optid);
        this.optid = optid;
        this.newModels = newModels;
        this.modelType = modelType;
    }

    public Map<Long, T> getNewModels() {
        return newModels;
    }

    public void setNewModels(Map<Long, T> newModels) {
        this.newModels = newModels;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }
}
