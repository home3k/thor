/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class BaseType<T extends BaseTypeField> {

    /**
     * 子model对应的父model的pk，主要应对如下场景：
     * 多model场景下，父model的更新（ADD/MOD）依赖子model的信息。特别是子model的pk为auto key的情况。
     */
    protected Long refModelPk;

    /**
     * 驻留的数据。
     */
    protected Object container;

    /**
     * Model的id
     *
     * @return model id
     */
    public abstract Long getId();

    public abstract void setId(Long id);

    /**
     * 该Model生命的字段
     *
     * @return
     */
    public abstract T[] getDeclareFields();

    public Long getRefModelPk() {
        return refModelPk;
    }

    public void setRefModelPk(Long refModelPk) {
        this.refModelPk = refModelPk;
    }

    public Object getContainer() {
        return container;
    }

    public void setContainer(Object container) {
        this.container = container;
    }

    /**
     * Model clone
     *
     * @return
     */
    public BaseType clone() {
        try {
            return (BaseType) BeanUtils.cloneBean(this);
        } catch (Exception e) {
            throw new IllegalStateException("Illgal class to clone."
                    , e);
        }
    }
}
