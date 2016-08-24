/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;

import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.context.ErrorContextHolder;

import java.util.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */

public class ErrorUtils<T, V> {

    /**
     * 保存验证过程中的错误，并返回验证通过的数据。
     *
     * @param <V>
     * @param errors
     * @return Map<Long,T>
     */
    public static <V> Map<Long, V> saveCheckResultErrors(
            Map<Long, CheckResult<V>> errors) {
        Map<Long, V> result = new LinkedHashMap<Long, V>();
        for (Long key : errors.keySet()) {
            CheckResult<V> cresult = errors.get(key);
            if (cresult.isErrorResult()) {
                ErrorContextHolder.setErrorContext(key, cresult.getError());
            } else {
                result.put(key, cresult.getData());
            }
        }
        return result;
    }

    /**
     * 合并后返回结果
     *
     * @param <V>
     * @param userid
     * @param opuid
     * @param models
     * @return Map<Long,CheckResult<V>>
     * @author sunkai
     * @date 2012-5-24
     */
    public static <V> Map<Long, CheckResult<V>> returnModels(Long userid,
                                                             Long opuid, Map<Long, V> models) {
        Map<Long, CheckResult<V>> result = new LinkedHashMap<Long, CheckResult<V>>();

        // 正常结果
        if (null != models) {
            for (Long key : models.keySet()) {
                V model = models.get(key);
                CheckResult<V> checkItem = new CheckResult<V>(model);
                result.put(key, checkItem);
            }
        }

        // 错误数据
        Map<Long, BizError> errors = ErrorContextHolder.getErrorContext();
        if (null != errors) {
            for (Long key : errors.keySet()) {
                BizError error = errors.get(key);
                CheckResult<V> checkItem = new CheckResult<V>(error);
                result.put(key, checkItem);
            }
        }

        // 清空errorholder
        ErrorContextHolder.clearErrorContext();

        return result;
    }

    /**
     * 填充error
     *
     * @param error
     * @param keys
     * @return Map<Long,BizError>
     */
    public static <V, N extends Number> Map<N, CheckResult<V>> errorRange(BizError error,
                                                                          Collection<N> keys) {
        Map<N, CheckResult<V>> result = new HashMap<N, CheckResult<V>>();
        for (N key : keys) {
            CheckResult<V> checkResult = new CheckResult<V>(error);
            result.put(key, checkResult);
        }
        return result;
    }

    public static <V> Map<Integer, CheckResult<V>> errorRange(BizError error,
                                                              Integer range) {
        Map<Integer, CheckResult<V>> result = new HashMap<Integer, CheckResult<V>>();
        for (int i = 1; i <= range; i++) {
            CheckResult<V> checkResult = new CheckResult<V>(error);
            result.put(i, checkResult);
        }
        return result;
    }

    /**
     * 保存处理过程中的错误。
     *
     * @param error
     * @param keys  void
     */
    public static void saveErrors(BizError error, Collection<Long> keys) {
        Map<Long, BizError> errors = new HashMap<Long, BizError>();
        for (Long key : keys) {
            errors.put(key, error);
        }
        ErrorContextHolder.setErrorContext(errors);
    }

    public static void saveError(BizError error, Long key) {
        saveErrors(error, Arrays.asList(key));
    }

    /**
     * 分片方法
     *
     * @param <V>
     * @param models
     * @param threshold
     * @return List<Map<Long,V>>
     */
    public static <V> List<Map<Long, V>> sharding(Map<Long, V> models,
                                                  int threshold) {
        if (models == null || models.size() == 0) {
            return Collections.emptyList();
        }
        List<Map<Long, V>> result = new ArrayList<Map<Long, V>>();
        Map<Long, V> item = null;
        int i = 0;
        for (Long key : models.keySet()) {
            if (i % threshold == 0) {
                //先往item中添加，当数据达到threshold,再把item插入到result里
                if (null != item) {
                    result.add(item);
                }
                item = new HashMap<Long, V>();
            }
            item.put(key, models.get(key));
            i++;
        }
        if (null != item) {
            result.add(item);
        }
        return result;
    }

    public static <T> List<List<T>> divideCollection(List<T> src,
                                                     int batchsize) {
        List<List<T>> ret = new ArrayList<List<T>>();
        if (src.size() > 0 && batchsize > 0) {

            List<T> tsrc = new ArrayList<T>(src);
            for (int i = 0; i < src.size(); i += batchsize) {
                ret.add(tsrc.subList(i, Math.min(i + batchsize, src.size())));
            }
        }
        return ret;

    }

}

