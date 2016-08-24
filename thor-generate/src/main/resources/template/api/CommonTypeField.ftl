/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.${pack}.dto;

/**
 * ${desc}列描述
 */
public enum ${Model}TypeField {

<#list fieldlist as field>
    /**
     * ${field.desc}
     */
    ${field.name},
</#list>

}
