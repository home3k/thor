/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class OptionLimit extends Option<Integer[]> {
    /**
     * 默认值
     */
    private static final int DEFAULT_LIMIT = 1000;

    public Integer[] getLimit() {
        return limit;
    }

    public void setLimit(Integer[] limit) {
        this.limit = limit;
    }

    /**
     */
    private Integer[] limit = new Integer[DEFAULT_LIMIT];

    @Override
    public OptionFunc func() {
        return OptionFunc.LIMIT;
    }

    @Override
    public Integer[] getOption() {
        return this.limit;
    }

    @Override
    public void setOption(Integer[] option) {
        this.limit = option;
    }
}
