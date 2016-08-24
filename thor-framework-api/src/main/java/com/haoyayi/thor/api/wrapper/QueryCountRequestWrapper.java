/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.CountRequest;
import com.haoyayi.thor.api.QueryRequest;
import com.haoyayi.thor.api.wrapper.AbstractWrapper;

import java.io.Serializable;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class QueryCountRequestWrapper<V extends ConditionField> extends AbstractWrapper implements Serializable {

    private CountRequest<V> countRequest;

    public CountRequest<V> getCountRequest() {
        return countRequest;
    }

    public void setCountRequest(CountRequest<V> countRequest) {
        this.countRequest = countRequest;
    }
}
