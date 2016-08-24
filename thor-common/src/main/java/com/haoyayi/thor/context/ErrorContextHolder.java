/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context;

import com.haoyayi.thor.common.BizError;

import java.util.HashMap;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ErrorContextHolder {

    /**
     * ThreadLocal Holder
     */
    private static final ThreadLocal<Map<Long, BizError>> contextHolder = new ThreadLocal<Map<Long, BizError>>();

    /**
     * @param key
     * @param bizError
     */
    public static void setErrorContext(Long key, BizError bizError) {
        Map<Long, BizError> errors = contextHolder.get();
        if (null == errors) {
            errors = new HashMap<Long, BizError>();
        }
        errors.put(key, bizError);
        contextHolder.set(errors);
    }

    /**
     * Set BizErrors TO ThreadLocal
     *
     * @param bizErrors void
     * @author sunkai
     */
    public static void setErrorContext(Map<Long, BizError> bizErrors) {
        Map<Long, BizError> errors = contextHolder.get();
        if (null == errors) {
            errors = new HashMap<Long, BizError>();
        }
        errors.putAll(bizErrors);
        contextHolder.set(errors);
    }

    /**
     * Get BizError FROM ThreadLocal
     *
     * @return Long
     * @author sunkai
     */
    public static Map<Long, BizError> getErrorContext() {
        return contextHolder.get();
    }

    /**
     * Clear ThreadLocal
     *
     * @author sunkai
     */
    public static void clearErrorContext() {
        contextHolder.remove();
    }

}

