/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.validate;

import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.common.CheckResult;

import java.util.List;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface ConditionValidator< C extends ConditionField> {

    CheckResult<List<ConditionPair<C>>> validate(List<ConditionPair<C>> conditions);

}
