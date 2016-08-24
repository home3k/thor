/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.lang.*;
import java.lang.Error;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class ModResponse<T extends BaseType> extends CommonResponse {
    /**
     * 返回数据
     */
    private Map<Long, T> data;

    /**
     * 错误结果
     */
    private Map<Long, Map<String, com.haoyayi.thor.api.Error>> errorInfo;

    public Map<Long, T> getData() {
        return data;
    }

    public void setData(Map<Long, T> data) {
        this.data = data;
    }

    public Map<Long, Map<String, com.haoyayi.thor.api.Error>> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(Map<Long, Map<String, com.haoyayi.thor.api.Error>> errorInfo) {
        this.errorInfo = errorInfo;
    }
}

