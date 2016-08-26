/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.listener;

import com.haoyayi.thor.event.AbstractModelEvent;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractSynListener<T extends AbstractModelEvent> extends AbstractListener<T> {

    @Override
    public void onApplicationEvent(T event) {
        if (event.getIsAsync() || !needProcess(event)) {
            return;
        }
        process(event);
    }

}
