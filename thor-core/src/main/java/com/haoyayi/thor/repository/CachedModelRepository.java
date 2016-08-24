/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.cache.Cache;
import com.haoyayi.thor.cache.impl.ThreadlocalCacheImpl;

/**
 * 支持Cache的Repo，暂时只支持thread local
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class CachedModelRepository<T extends BaseType> extends BaseModelRepository implements InitializingBean {
    /**
     * thread local cache
     */
    protected Cache threadlocalCache;

    /**
     * Cache 初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        threadlocalCache = ThreadlocalCacheImpl.getInstance();
    }

    protected String generateCacheKey(Long id) {
        return getModelType().toString() + "-" + id;
    }
    
    protected String generateDelCacheKey(Long id) {
        return "DEL-" + getModelType().toString() + "-" + id;
    }

    /**
     * Cache中删除data
     *
     * @param models
     */
    protected void removeFromThreadLocal(Map<Long, T> models) {
        Map<String, Object> cacheData = new HashMap<String, Object>();
        Map<String, Object> delCacheData = new HashMap<String, Object>();
        for (Long id : models.keySet()) {
            cacheData.put(generateCacheKey(id), models.get(id));
            delCacheData.put(generateDelCacheKey(id), models.get(id));
        }
        threadlocalCache.remove(cacheData.keySet());
        threadlocalCache.set(delCacheData);
    }

    protected Set<String> batchGenerateCacheKey(Set<Long> ids) {
        Set<String> keys = new HashSet<String>();
        for (Long id : ids) {
            keys.add(generateCacheKey(id));
        }
        return keys;
    }
    
    protected Set<String> batchGenerateDelCacheKey(Set<Long> ids) {
        Set<String> keys = new HashSet<String>();
        for (Long id : ids) {
            keys.add(generateDelCacheKey(id));
        }
        return keys;
    }

    protected Long getIdFromKey(String key) {
        return Long.parseLong(key.substring((getModelType() + "-").length()));
    }
    
    protected Long getDelIdFromKey(String key) {
    	return Long.parseLong(key.substring(("DEL-" + getModelType() + "-").length()));
    }

    /**
     * 从threadlocal中获得data
     *
     * @param ids
     * @return
     */
    protected Map<Long, T> getModelFromThreadlocal(Set<Long> ids) {
        Map<String, Object> data = threadlocalCache.get(batchGenerateCacheKey(ids));
        Map<Long, T> result = new HashMap<Long, T>();
        for (String key : data.keySet()) {
            result.put(getIdFromKey(key), (T) ((T) data.get(key)).clone());
        }
        return result;
    }
    
    protected Map<Long, T> getDelModelFromThreadlocal(Set<Long> ids) {
    	Map<String, Object> data = threadlocalCache.get(batchGenerateDelCacheKey(ids));
        Map<Long, T> result = new HashMap<Long, T>();
        for (String key : data.keySet()) {
            result.put(getDelIdFromKey(key), (T) ((T) data.get(key)).clone());
        }
        return result;
    }
    
    /**
     * 更新data到cache中
     *
     * @param result
     */
    protected void saveModel2Cache(Map<Long, T> result) {
        Map<String, Object> result4cache = new HashMap<String, Object>();
        for (T t : result.values()) {
            result4cache.put(generateCacheKey(t.getId()), t);
        }
        threadlocalCache.set(result4cache);
    }


}
