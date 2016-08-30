/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.validate;

import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.impl.base.OpType;

import java.util.Map;

/**
 * Command验证接口
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface Validator<R extends BaseTypeField> {

    Map<Long, CheckResult<Map<R, Object>>> validate(Map<Long, Map<R, Object>> context, OpType action);

}
