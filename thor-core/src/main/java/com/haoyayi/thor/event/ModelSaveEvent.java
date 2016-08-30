/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.event;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 模型修改事件
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModelSaveEvent<T extends BaseType, C extends BaseTypeField> extends AbstractModelEvent {

    private Map<Long, T> oldModel;

    private Map<Long, T> newModel;

    private Map<Long, Map<C, Object>> changeMap;

    private Map<Long, Set<C>> changeFields;
    
    private Map<Long, Set<C>> allFields;

    private String modelType;

    public ModelSaveEvent(Object source) {
        super(source);
    }

    public ModelSaveEvent(Map<Long, T> oldmodels, Map<Long, T> newmodels, Map<Long, Map<C, Object>> changeMap, String modelType) {
        super(newmodels);
        this.changeMap = changeMap;
        this.oldModel = oldmodels;
        this.newModel = newmodels;
        this.modelType = modelType;
        this.freshChangeFields(changeMap);
    }

    private void freshChangeFields(Map<Long, Map<C, Object>> changeMap) {
        this.changeFields = new HashMap<Long, Set<C>>();
        for (Long id : changeMap.keySet()) {
            this.changeFields.put(id, changeMap.get(id).keySet());
        }
    }

    public Map<Long, T> getOldModel() {
        return oldModel;
    }

    public void setOldModel(Map<Long, T> oldModel) {
        this.oldModel = oldModel;
    }

    public Map<Long, T> getNewModel() {
        return newModel;
    }

    public void setNewModel(Map<Long, T> newModel) {
        this.newModel = newModel;
    }

    public Map<Long, Map<C, Object>> getChangeMap() {
        return changeMap;
    }

    public void setChangeMap(Map<Long, Map<C, Object>> changeMap) {
        this.changeMap = changeMap;
    }

    @Override
    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public Map<Long, Set<C>> getChangeFields() {
        return changeFields;
    }

    public void setChangeFields(Map<Long, Set<C>> changeFields) {
        this.changeFields = changeFields;
    }

	public Map<Long, Set<C>> getAllFields() {
		return allFields;
	}

	public void setAllFields(Map<Long, Set<C>> allFields) {
		this.allFields = allFields;
	}
    
}
