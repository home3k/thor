/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz.dict;

import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.api.dict.dto.${Model}TypeField;
import com.haoyayi.thor.processor.AbstractColumnProcessor;
import org.springframework.stereotype.Service;

/**
 * ${desc}模型转换
 */
@Service
public class ${Model}ConvertBiz extends AbstractColumnProcessor<${Model}TypeField> {

    @Override
    protected ModelType getModelType() {
        return ModelType.${model};
    }

    public ${Model}TypeField[] getFields() {
        return ${Model}TypeField.values();
    }

    public ${Model}TypeField getRefModelPk() {
        return ${Model}TypeField.refModelPk;
    }

    @Override
    public Boolean isDict() {
        return true;
    }

}
