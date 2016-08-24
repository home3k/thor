/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.event;

import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.context.BizContextDict;
import com.haoyayi.thor.context.BizContextHolder;
import com.haoyayi.thor.context.InvokeContextDict;
import com.haoyayi.thor.context.InvokeContextHolder;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;

/**
 * Model Event
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractModelEvent extends ApplicationEvent {

    protected HashMap<InvokeContextDict, Object> invokeContext;

    protected HashMap<BizContextDict, Object> bizContext;

    protected Long optid;
    
    private Boolean isAsync = Boolean.FALSE;

    public abstract ModelType getModelType();

    public AbstractModelEvent(Object source) {
        super(source);
        // 多线程情况下，通过event，进行context clone。
        setBizContext(BizContextHolder.getInstance().getBizMap());
        setInvokeContext(InvokeContextHolder.getInstance().getInvokeMap());
    }

    public Long getOptid() {
        return optid;
    }

    public void setOptid(Long optid) {
        this.optid = optid;
    }

    public HashMap<InvokeContextDict, Object> getInvokeContext() {
        return invokeContext;
    }

    public void setInvokeContext(HashMap<InvokeContextDict, Object> invokeContext) {
        this.invokeContext = invokeContext;
    }

    public HashMap<BizContextDict, Object> getBizContext() {
        return bizContext;
    }

    public void setBizContext(HashMap<BizContextDict, Object> bizContext) {
        this.bizContext = bizContext;
    }

	public Boolean getIsAsync() {
		return isAsync;
	}

	public void setIsAsync(Boolean isAsync) {
		this.isAsync = isAsync;
	}
}
