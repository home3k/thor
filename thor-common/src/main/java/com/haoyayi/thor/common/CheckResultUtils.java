/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class CheckResultUtils {

    /**
     * @param checkResultList
     * @param <T>
     * @return
     */
    public static <T> Map<Long, Map<T, Object>> getOkData(Map<Long, CheckResult<Map<T, Object>>> checkResultList) {
        Map<Long, Map<T, Object>> result = new HashMap<Long, Map<T, Object>>();
        if (checkResultList == null || checkResultList.size() == 0) {
            return result;
        }
        for (Long key : checkResultList.keySet()) {
            CheckResult<Map<T, Object>> checkResultItem = checkResultList.get(key);
            if (!checkResultItem.isErrorResult()) {
                result.put(key, checkResultItem.getData());
            }
        }
        return result;
    }


    /**
     * @param <T>
     * @param base
     * @param newData
     * @return Map<Long,CheckResult<T>>
     */
    public static <T> Map<Long, CheckResult<Map<T, Object>>> mergeCheckResult(Map<Long, CheckResult<Map<T, Object>>> base, Map<Long, CheckResult<Map<T, Object>>> newData) {
        if (base == null || base.size() == 0) {
            return base;
        }
        if (newData == null || newData.size() == 0) {
            return base;
        }
        for (Long key : base.keySet()) {
            CheckResult<Map<T, Object>> baseCheckResult = base.get(key);
            if (baseCheckResult.isErrorResult()) {
                continue;
            }
            CheckResult<Map<T, Object>> newCheckResult = newData.get(key);
            base.put(key, newCheckResult);

        }
        return base;
    }

}
