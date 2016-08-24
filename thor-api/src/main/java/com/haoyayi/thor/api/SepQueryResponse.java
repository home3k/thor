/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class SepQueryResponse<X> extends CommonResponse {
    /**
     * 返回数据, 任意数据格式
     */
    private X data;

    public X getData() {
        return data;
    }

    public void setData(X data) {
        this.data = data;
    }
}
