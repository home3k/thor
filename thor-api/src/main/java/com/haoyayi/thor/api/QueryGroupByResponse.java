/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class QueryGroupByResponse extends CommonResponse {

    private Map<String, Object>[] data;

    public Map<String, Object>[] getData() {
        return data;
    }

    public void setData(Map<String, Object>[] data) {
        this.data = data;
    }
}
