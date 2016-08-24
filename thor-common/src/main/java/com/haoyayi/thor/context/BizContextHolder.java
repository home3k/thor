/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context;

import java.util.HashMap;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class BizContextHolder {

    /**
     * Common Holder
     */
    private static ThreadLocal<HashMap<BizContextDict, Object>> envstore = new ThreadLocal<HashMap<BizContextDict, Object>>() {
        @Override
        protected HashMap<BizContextDict, Object> initialValue() {
            return new HashMap<BizContextDict, Object>();
        }
    };

    /**
     *
     */
    private static BizContextHolder instance = new BizContextHolder();

    /**
     *
     * @return
     */
    public synchronized static BizContextHolder getInstance() {
        return instance;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setBizContext(BizContextDict key, Object value) {
        envstore.get().put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getBizContext(BizContextDict key){
        return envstore.get().get(key);
    }

    public Object getBizContextAsString(BizContextDict key){
        Object context = envstore.get().get(key);
        if (context == null) {
            return "-";
        } else {
            return context.toString();
        }

    }

    public HashMap<BizContextDict, Object> getBizMap() {
        return envstore.get();
    }

    public void setBizMap(HashMap<BizContextDict, Object> map) {
        envstore.get().putAll(map);
    }

    /**
     *
     */
    public void clear() {
        envstore.remove();
    }
}
