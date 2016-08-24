/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.model;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModelPair<V extends BaseType, C extends BaseTypeField> {

    private Long id;

    private V oldModel;
    private V newModel;

    public ModelPair() {}

    public ModelPair(V oldModel, V newModel) {
        this.oldModel = oldModel;
        this.newModel = newModel;
    }

    public ModelPair(Long id, V oldModel, V newModel) {
        this.id = id;
        this.oldModel = oldModel;
        this.newModel = newModel;
    }

    public V getOldModel() {
        return oldModel;
    }

    public void setOldModel(V oldModel) {
        this.oldModel = oldModel;
    }

    public V getNewModel() {
        return newModel;
    }

    public void setNewModel(V newModel) {
        this.newModel = newModel;
    }

    public C[] getAllField() {
        return (C[]) newModel.getDeclareFields();
    }

    public Map<C, Object> getDiffModifyField() {
        Map<C, Object> result = new HashMap<C, Object>();
        if (oldModel == null || newModel == null) {
            throw new IllegalArgumentException("model for diff is null,old:" + oldModel + " new:" + newModel);
        }
        try {
            for (C fieldname : getAllField()) {
                Object oldFieldValue = PropertyUtils.getProperty(oldModel, fieldname.toString());
                Object newFieldValue = PropertyUtils.getProperty(newModel, fieldname.toString());
                if (ObjectUtils.equals(oldFieldValue, newFieldValue)) {
                    continue;
                } else {
                    result.put(fieldname, newFieldValue);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("diff model error", e);
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}