/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.repository;

import com.haoyayi.thor.api.ModelType;

/**
 * 基本Repository
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class BaseModelRepository {
    /**
     * Repo的模型类型
     *
     * @return
     */
    protected abstract ModelType getModelType();

}
