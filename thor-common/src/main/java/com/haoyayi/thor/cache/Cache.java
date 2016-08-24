/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface Cache {

    void asynRemove(Collection<String> keys);

    void remove(Collection<String> keys);

    void asynSet(Map<String, Object> kv);

    void set(Map<String, Object> kv);

    void set(Map<String, Object> items, Integer expTime);

    void asynSet(Map<String, Object> items, Integer expTime);

    <T> Map<String, T> get(Set<String> keys, Class<T> clazz);

    Map<String, Object> get(Set<String> keys);
}
