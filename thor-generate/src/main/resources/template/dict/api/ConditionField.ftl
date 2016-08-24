/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.dict.dto;

import com.haoyayi.thor.api.ConditionField;

/**
 * ${desc}条件查询
 */
public enum ${Model}ConditionField implements ConditionField {

    <#list fieldlist as field>
    /**
     * ${field.desc}
     */
    ${field.name},
    </#list>

}
