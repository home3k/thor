/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.Map;

/**
 * 添加返回结果
 *
 * @param <T>
 * @author home3k (sunkai@51haoyayi.com)
 */
public class AddResponse<T extends BaseType> extends CommonResponse {

    /**
     * 结果数据，按照数组进行返回
     */
    private Map<Integer, T> data;
    /**
     * 错误信息
     */
    private Map<Integer, Map<String, com.haoyayi.thor.api.Error>> errorInfo;

    public Map<Integer, T> getData() {
        return data;
    }

    public void setData(Map<Integer, T> data) {
        this.data = data;
    }

    public Map<Integer, Map<String, com.haoyayi.thor.api.Error>> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(Map<Integer, Map<String, com.haoyayi.thor.api.Error>> errorInfo) {
        this.errorInfo = errorInfo;
    }
}
