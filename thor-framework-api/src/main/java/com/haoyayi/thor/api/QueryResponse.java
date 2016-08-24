/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.List;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class QueryResponse<T extends BaseType> extends CommonResponse {
    /**
     * 返回数据
     */
    private List<T> data;
    /**
     * 错误数据
     */
    private Map<Long, com.haoyayi.thor.api.Error> errorInfo;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<Long, com.haoyayi.thor.api.Error> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(Map<Long, com.haoyayi.thor.api.Error> errorInfo) {
        this.errorInfo = errorInfo;
    }
}

