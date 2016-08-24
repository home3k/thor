/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;

/**
 * Desc:
 * User: home3k
 * Date: 14/12/18-下午5:48
 */
public class CostTime {

    private transient long start;

    public CostTime start() {
        this.start = System.currentTimeMillis();
        return this;
    }

    public long cost() {
        return System.currentTimeMillis() - start;
    }
}
