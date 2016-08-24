/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz.dict;

import com.haoyayi.thor.api.dict.dto.${Model}ConditionField;
import com.haoyayi.thor.api.dict.dto.${Model}Type;
import com.haoyayi.thor.api.dict.dto.${Model}TypeField;
import com.haoyayi.thor.query.AbstractQueryFacade;
import com.haoyayi.thor.repository.ModelConditionQueryRepository;
import com.haoyayi.thor.repository.dict.${Model}Repository;
import com.haoyayi.thor.validate.ConditionValidator;
import com.haoyayi.thor.validate.dict.${Model}Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.haoyayi.thor.api.ModelType;

/**
 * ${desc}查询业务层
 */
@Service
public class ${Model}QueryBiz extends AbstractQueryFacade<${Model}Type, ${Model}TypeField, ${Model}ConditionField> {

    @Autowired
    private ${Model}Validator ${model}Validator;

    protected ConditionValidator<${Model}ConditionField> getValidator() {
        return this.${model}Validator;
    }

    @Override
    protected ModelType getModelType() {
        return ModelType.${model};
    }

}
