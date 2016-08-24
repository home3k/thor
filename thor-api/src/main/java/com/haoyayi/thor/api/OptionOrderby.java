/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class OptionOrderby<T> extends Option<Map<T, Boolean>> {
    public Map<T, Boolean> getOrderby() {
        return orderby;
    }

    public void setOrderby(Map<T, Boolean> orderby) {
        this.orderby = orderby;
    }

    /**
     * 排序方式, boolean 为降序
     */
    private Map<T, Boolean> orderby;

    @Override
    public OptionFunc func() {
        return OptionFunc.ORDERBY;
    }

    @Override
    public Map<T, Boolean> getOption() {
        return this.orderby;
    }

    @Override
    public void setOption(Map<T, Boolean> option) {
        this.orderby = option;
    }
}
