/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import java.io.Serializable;

import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.DelRequest;

/**
 * optid & *request wrapper
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class DelRequestWrapper<T extends BaseTypeField> extends GenericRequestWrapper implements Serializable {

    private DelRequest<T>[] delRequests;

    public DelRequest<T>[] getDelRequests() {
        return delRequests;
    }

    public void setDelRequests(DelRequest<T>[] delRequests) {
        this.delRequests = delRequests;
    }
}
