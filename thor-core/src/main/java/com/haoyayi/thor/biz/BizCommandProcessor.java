/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz;

import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.common.CheckResult;

import java.util.Map;

/**
 * 业务处理对象
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface BizCommandProcessor<T extends BaseType, V extends BaseTypeField> {

    /**
     * @param dentists
     * @return
     */
    Map<Long, CheckResult<T>> add(Map<Long, Map<V, Object>> dentists);

    /**
     * @param dentists
     * @return
     */
    Map<Long, CheckResult<T>> mod(Map<Long, Map<V, Object>> dentists);

    /**
     *
     * @param dentists
     * @return
     */
    Map<Long, CheckResult<T>> del(Map<Long, Map<V, Object>> dentists);

}
