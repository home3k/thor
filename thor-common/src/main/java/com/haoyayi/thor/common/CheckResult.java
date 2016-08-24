/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.common;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 * @description TODO
 */
public class CheckResult<T> {

    private T data;

    private BizError error;

    public T getData() {
        return data;
    }

    public CheckResult setData(T data) {
        this.data = data;
        return this;
    }

    public CheckResult(BizError error) {
        this.error = error;
    }

    public CheckResult(T data) {
        this.data = data;
    }

    public BizError getError() {
        return error;
    }

    public CheckResult setError(BizError error) {
        this.error = error;
        return this;
    }

    public boolean isErrorResult() {
        return error != null;
    }

    public CheckResult setCheckResult(CheckResult<T> newdata) {
        if (newdata != null) {
            this.data = newdata.getData();
            this.error = newdata.getError();
        }
        return this;
    }

}

