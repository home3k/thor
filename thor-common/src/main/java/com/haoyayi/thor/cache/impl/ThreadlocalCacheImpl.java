/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.cache.impl;

import com.haoyayi.thor.cache.Cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */

public class ThreadlocalCacheImpl implements Cache {

    private static ThreadLocal<HashMap<String, Object>> cache = new ThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    private static ThreadlocalCacheImpl instance = new ThreadlocalCacheImpl();

    public synchronized static ThreadlocalCacheImpl getInstance() {
        return instance;
    }

    private ThreadlocalCacheImpl() {
    }

    public void clear() {
        cache.remove();
    }

    public Object get(String key) {
        return cache.get().get(key);
    }

    public Map<String, Object> get(Set<String> keys) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : keys) {
            if (cache.get().containsKey(key))
                result.put(key, cache.get().get(key));
        }
        return result;
    }

    public void set(Map<String, Object> data) {
        for (String key : data.keySet()) {
            if (data.get(key) != null) {
                cache.get().put(key, data.get(key));
            }
        }
    }

    public void set(String key, Object value) {
        cache.get().put(key, value);
    }

    public void set(String key, Object value, Integer expTime) {
        set(key, value);
    }

    public void set(Map<String, Object> items, Integer expTime) {
        set(items);
    }


    public Object get(String key, Class<?> clazz) {
        return this.get(key);
    }

    public boolean remove(Set<String> keys) {
        for (String key : keys) {
            cache.get().remove(key);
        }
        return true;
    }

    public boolean remove(String key) {
        cache.get().remove(key);
        return true;
    }

    @Override
    public void asynSet(Map<String, Object> items) {
        this.set(items);
    }

    @Override
    public void asynSet(Map<String, Object> items, Integer expTime) {
        this.set(items, expTime);
    }

    @Override
    public void asynRemove(Collection<String> keys) {
        this.remove(keys);
    }

    @Override
    public void remove(Collection<String> keys) {
        for (String key : keys)
            this.remove(key);
    }

    @Override
    public <T> Map<String, T> get(Set<String> keys, Class<T> clazz) {
        return (Map<String, T>) this.get(keys);
    }
}
