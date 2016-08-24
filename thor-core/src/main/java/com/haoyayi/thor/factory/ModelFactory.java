/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.factory;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.model.ModelPair;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public interface ModelFactory<T extends BaseType, V extends BaseTypeField> {

    /**
     * 构建new Model.
     *
     * @param optid
     * @param context
     * @return
     */
    Map<String, Map<Long, T>> createModel(Long optid, Map<Long, Map<V, Object>> context);

    /**
     * @param optid
     * @param context
     * @return
     */
    Map<String, Map<Long, ModelPair<T, V>>> modModel(Long optid, Map<Long, Map<V, Object>> context);

    /**
     * @param optid
     * @param context
     * @return
     */
    Map<Long, T> delModel(Long optid, Map<Long, Map<V, Object>> context);

}
