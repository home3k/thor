/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.context;

import java.util.HashMap;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CodegenContextHolder {

    /**
     * Common Holder
     */
    private static ThreadLocal<HashMap<String, Object>> envstore = new ThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    /**
     *
     */
    private static CodegenContextHolder instance = new CodegenContextHolder();

    /**
     *
     * @return
     */
    public synchronized static CodegenContextHolder getInstance() {
        return instance;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setCodegenContext(String key, Object value) {
        envstore.get().put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getCodegenContext(String key){
        return envstore.get().get(key);
    }

    public HashMap<String, Object> getBizMap() {
        return envstore.get();
    }

    public void setBizMap(HashMap<String, Object> map) {
        envstore.get().putAll(map);
    }

    /**
     *
     */
    public void clear() {
        envstore.remove();
    }
}

