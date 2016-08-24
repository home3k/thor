/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.${model}.dto;

import com.haoyayi.thor.api.BaseTypeField;

/**
 * ${desc}列描述
 */
public enum ${Model}TypeField implements BaseTypeField {

    /**
     * 驻留容器信息
     */
    container,

    /**
     * 引用的model pk
     */
    refModelPk,
<#list fieldlist as field>

    /**
     * ${field.desc}
     */
    ${field.name},
</#list>

}
