/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CountRequest<C extends ConditionField> extends CommonRequest  {

    /**
     * 查询条件
     */
    private ConditionPair<C>[] conditions;

    public ConditionPair<C>[] getConditions() {
        return conditions;
    }

    public void setConditions(ConditionPair<C>[] conditions) {
        this.conditions = conditions;
    }
}
