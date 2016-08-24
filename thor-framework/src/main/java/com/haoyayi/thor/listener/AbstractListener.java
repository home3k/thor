/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.listener;

import com.haoyayi.thor.event.AbstractModelEvent;
import com.haoyayi.thor.api.ModelType;
import org.springframework.context.ApplicationListener;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */

public abstract class AbstractListener<T extends AbstractModelEvent> implements ApplicationListener<T> {


    protected abstract ModelType getModelType();

    protected Boolean needProcess(T event){
        if (event.getModelType() != getModelType()) {
            return false;
        }
        return true;
    }

    protected abstract void process(T event);

}

