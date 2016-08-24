/*
* Copyright 2014 51haoyayi.com Inc Limited.
* All rights reserved.
*/

package com.haoyayi.thor.api.${pack}.dto;

import java.util.Date;

/**
 * ${desc}数据描述
 */
public class ${Model}Type {

    <#list fieldlist as field>
    /**
     * ${field.desc}
     */
    private ${field.fieldType.javaType} ${field.name};

    </#list>

    public ${Model}TypeField[] getDeclareFields() {
        return ${Model}TypeField.values();
    }

    ${setStr}

}
