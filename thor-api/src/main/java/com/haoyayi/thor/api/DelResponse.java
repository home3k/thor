/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.lang.*;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class DelResponse<T extends BaseType> extends CommonResponse {
    /**
     * 删除返回结果
     */
    private Map<Long, T> data;
    /**
     * 错误数据
     */
    private Map<Long, com.haoyayi.thor.api.Error> errorInfo;

    /**
     * @return
     */
    public Map<Long, T> getData() {
        return data;
    }

    public void setData(Map<Long, T> data) {
        this.data = data;
    }

    /**
     * @return
     */
    public Map<Long, com.haoyayi.thor.api.Error> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(Map<Long, com.haoyayi.thor.api.Error> errorInfo) {
        this.errorInfo = errorInfo;
    }
}

