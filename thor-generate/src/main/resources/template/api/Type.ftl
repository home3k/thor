/*
* Copyright 2014 51haoyayi.com Inc Limited.
* All rights reserved.
*/

package com.haoyayi.thor.api.${model}.dto;

import com.haoyayi.thor.api.BaseType;

import java.util.Date;
import java.util.Map;
<#list refmodellist as rm>
import com.haoyayi.thor.api.${rm.refModel}.dto.*;
</#list>
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ${desc}数据描述
 */
${ignore}
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ${Model}Type extends BaseType<${Model}TypeField> {

    <#list fieldlist as field>

    /**
     * ${field.desc}
     */
    private ${field.fieldType.javaType} ${field.name};
    </#list>

    @Override
    public Long getId() {
        return this.${pk};
    }

    @Override
    public void setId(Long id) {
    this.${pk} = id;
    }

    @Override
    public ${Model}TypeField[] getDeclareFields() {
        return ${Model}TypeField.values();
    }

    ${setStr}

}
