/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context;

import java.util.HashMap;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class InvokeContextHolder {

    public static final String CURRENT_MODULDE = "thor";

    /**
     * Common Holder
     */
    private static ThreadLocal<HashMap<InvokeContextDict, Object>> envstore = new ThreadLocal<HashMap<InvokeContextDict, Object>>() {
        @Override
        protected HashMap<InvokeContextDict, Object> initialValue() {
            return new HashMap<InvokeContextDict, Object>();
        }
    };

    /**
     *
     */
    private static InvokeContextHolder instance = new InvokeContextHolder();

    /**
     *
     * @return
     */
    public synchronized static InvokeContextHolder getInstance() {
        return instance;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setBizContext(InvokeContextDict key, Object value) {
        envstore.get().put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getBizContext(InvokeContextDict key){
        return envstore.get().get(key);
    }

    public String getBizContextAsString(InvokeContextDict key){
        Object context = envstore.get().get(key);
        if (context == null) {
            return "-";
        } else {
            return context.toString();
        }

    }

    public HashMap<InvokeContextDict, Object> getInvokeMap() {
        return envstore.get();
    }

    public void setInvokeMap(HashMap<InvokeContextDict, Object> map) {
        envstore.get().putAll(map);
    }

    /**
     *
     */
    public void clear() {
        envstore.remove();
    }

}
