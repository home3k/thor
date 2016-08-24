/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ModelType;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface ModelProcessor<T extends BaseType, V extends BaseTypeField> {
    /**
     *
     * @param context
     * @return
     */
    Map<Long, T> process(Long optid, ModelType model,  Map<Long, Map<V, Object>> context);
}
