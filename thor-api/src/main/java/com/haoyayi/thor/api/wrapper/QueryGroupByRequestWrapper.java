/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.QueryGroupByRequest;

import java.io.Serializable;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class QueryGroupByRequestWrapper<V extends ConditionField, F extends BaseTypeField> extends AbstractWrapper implements Serializable {

    private QueryGroupByRequest<V, F> groupByRequest;

    public QueryGroupByRequest<V, F> getGroupByRequest() {
        return groupByRequest;
    }

    public void setGroupByRequest(QueryGroupByRequest<V, F> groupByRequest) {
        this.groupByRequest = groupByRequest;
    }
}
