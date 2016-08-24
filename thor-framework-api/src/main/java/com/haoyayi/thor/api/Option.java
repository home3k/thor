/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 * @description
 */
public abstract class Option<T> {

    public static final String OPTION_LIMIT_OFFSET = "offset";
    public static final String OPTION_LIMIT_NUM = "num";
    public static final String OPTION_ORDERBY = "orderby";

    /**
     * option函数
     *
     * @return
     */
    public abstract OptionFunc func();

    /**
     * @return
     */
    public abstract T getOption();

    /**
     * @param option
     */
    public abstract void setOption(T option);

}
