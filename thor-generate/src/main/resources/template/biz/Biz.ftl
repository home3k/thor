/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz.${model};

import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;
import com.haoyayi.thor.biz.AbstractBizCommandProccessor;
import com.haoyayi.thor.biz.BizCommandProcessor;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.factory.ModelFactory;
import com.haoyayi.thor.factory.${model}.${Model}ModelFactory;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.repository.ModelRepository;
import com.haoyayi.thor.repository.${model}.${Model}Repository;
import com.haoyayi.thor.validate.Validator;
import com.haoyayi.thor.validate.${model}.${Model}Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.haoyayi.thor.api.ModelType;

import java.util.Map;

/**
 * ${desc}业务层
 */
@Service
public class ${Model}Biz extends AbstractBizCommandProccessor<${Model}Type, ${Model}TypeField> implements BizCommandProcessor<${Model}Type, ${Model}TypeField> {

    @Autowired
    private ${Model}Validator ${model}Validator;

    @Override
    protected Validator<${Model}TypeField> getValidator() {
        return ${model}Validator;
    }

    @Override
    public Map<Long, CheckResult<${Model}Type>> add(Long optid, Map<Long, Map<${Model}TypeField, Object>> ${model}s) {
        return build(optid, ${model}s, OpType.ADD);
    }

    @Override
    public Map<Long, CheckResult<${Model}Type>> mod(Long optid, Map<Long, Map<${Model}TypeField, Object>> ${model}s) {
        return build(optid, ${model}s, OpType.MOD);
    }

    @Override
    public Map<Long, CheckResult<${Model}Type>> del(Long optid, Map<Long, Map<${Model}TypeField, Object>> ${model}s) {
        return build(optid, ${model}s, OpType.DEL);
    }

    protected ModelType getModelType() {
        return ModelType.${model};
    }

    @Override
    protected Map<Long, ${Model}Type> renderModel(Map<Long, ${Model}Type> models) {
        return models;
    }

}
