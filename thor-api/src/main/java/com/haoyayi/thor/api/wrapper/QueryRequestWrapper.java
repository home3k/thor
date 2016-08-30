/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.QueryRequest;

import java.io.Serializable;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class QueryRequestWrapper<T extends BaseTypeField, V extends ConditionField> extends GenericRequestWrapper implements Serializable {

    private QueryRequest<V> queryRequest;

    public QueryRequest<V> getQueryRequest() {
        return queryRequest;
    }

    public void setQueryRequest(QueryRequest<V> queryRequest) {
        this.queryRequest = queryRequest;
    }
}
