/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz.${model};

import java.util.Map;

import com.haoyayi.thor.api.${model}.dto.${Model}ConditionField;
import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;
import com.haoyayi.thor.query.AbstractQueryFacade;
import com.haoyayi.thor.repository.ModelConditionQueryRepository;
import com.haoyayi.thor.repository.${model}.${Model}Repository;
import com.haoyayi.thor.validate.ConditionValidator;
import com.haoyayi.thor.validate.${model}.${Model}Validator;
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
