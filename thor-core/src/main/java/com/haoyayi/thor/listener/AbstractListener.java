/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.listener;

import com.haoyayi.thor.ModelAware;
import com.haoyayi.thor.event.AbstractModelEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */

public abstract class AbstractListener<T extends AbstractModelEvent> implements ApplicationListener<T>, ModelAware {


    protected Boolean needProcess(T event){
        if (!event.getModelType().equals(getModelType())) {
            return false;
        }
        return true;
    }

    protected abstract void process(T event);

}

