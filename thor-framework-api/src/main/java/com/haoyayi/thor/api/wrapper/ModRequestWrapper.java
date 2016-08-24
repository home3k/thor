/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ModRequest;
import com.haoyayi.thor.api.wrapper.AbstractWrapper;

import java.io.Serializable;

/**
 * optid & *request wrapper
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModRequestWrapper<T extends BaseTypeField> extends AbstractWrapper implements Serializable {

    private ModRequest<T>[] modRequests;

    public ModRequest<T>[] getModRequests() {
        return modRequests;
    }

    public void setModRequests(ModRequest<T>[] modRequests) {
        this.modRequests = modRequests;
    }
}
