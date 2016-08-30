/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.AddRequest;
import com.haoyayi.thor.api.BaseTypeField;

import java.io.Serializable;

/**
 * optid & *request wrapper
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class AddRequestWrapper<T extends BaseTypeField> extends GenericRequestWrapper implements Serializable {

    private AddRequest<T>[] addRequests;

    public AddRequest<T>[] getAddRequests() {
        return addRequests;
    }

    public void setAddRequests(AddRequest<T>[] addRequests) {
        this.addRequests = addRequests;
    }
}
