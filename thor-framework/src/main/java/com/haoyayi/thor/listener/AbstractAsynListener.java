/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.listener;

import com.haoyayi.thor.context.BizContextHolder;
import com.haoyayi.thor.context.InvokeContextHolder;
import com.haoyayi.thor.event.AbstractModelEvent;
import org.springframework.scheduling.annotation.Async;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractAsynListener<T extends AbstractModelEvent> extends AbstractListener<T> {

    @Override
    @Async
    public void onApplicationEvent(T event) {
        try {
            cloneContext(event);
            if (!event.getIsAsync() || !needProcess(event)) {
                return;
            }
            process(event);
        } finally {
           clear();
        }


    }

    private void clear() {
        InvokeContextHolder.getInstance().clear();
        BizContextHolder.getInstance().clear();
    }

    /**
     * 进行context clone。
     *
     * @param event
     */
    private void cloneContext(T event) {
        InvokeContextHolder.getInstance().setInvokeMap(event.getInvokeContext());
        BizContextHolder.getInstance().setBizMap(event.getBizContext());
    }

}