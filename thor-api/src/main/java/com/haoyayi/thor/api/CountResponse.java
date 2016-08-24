/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;


/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CountResponse extends CommonResponse{

    private Long data;

    public Long getData() {
        return data;
    }

    public void setData(Long count) {
        this.data = count;
    }
}
