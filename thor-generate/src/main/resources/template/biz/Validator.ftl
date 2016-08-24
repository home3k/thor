/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.validate.${model};

import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.${model}.dto.${Model}ConditionField;
import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.validate.AbstractValidator;
import org.springframework.stereotype.Service;
import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.service.${model}.${Model}Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.haoyayi.thor.api.ErrorCode;

import java.util.HashMap;
import java.util.Map;

/**
 * ${desc}校验器
 */
@Service
public class ${Model}Validator extends AbstractValidator<${Model}Type, ${Model}TypeField, ${Model}ConditionField> {

    @Autowired
    ${Model}Service ${model}Service;

    protected ModelType getModelType() {
        return ModelType.${model};
    }

    @Override
    protected Map<Long, CheckResult<Map<${Model}TypeField, Object>>> validateMod(Long optid, Map<Long, Map<${Model}TypeField, Object>> context) {
        Map<Long, CheckResult<Map<${Model}TypeField, Object>>> resultMap=new HashMap<Long, CheckResult<Map<${Model}TypeField, Object>>>();
        Map<Long, Boolean> idCheckResult = ${model}Service.get${Model}Exist(context.keySet());
        for (Long key : context.keySet()) {
            if (!idCheckResult.get(key)) {
                resultMap.put(key, new CheckResult<Map<${Model}TypeField, Object>>(BizError.getBizError(ErrorCode.ERROR_EXIST_ERROR, "${Model} " + ${Model}TypeField.${pk}.name() + " not exist.", ${Model}TypeField.${pk}.name())));
                continue;
            }
            Map<${Model}TypeField, Object> map=context.get(key);
            CheckResult<Map<${Model}TypeField, Object>> checkResult=new CheckResult<Map<${Model}TypeField, Object>>(map);
            // TODO 默认校验通过
            resultMap.put(key,checkResult);
        }
        return resultMap;
    }

    @Override
    //TODO 添加验证有待完善
    protected Map<Long, CheckResult<Map<${Model}TypeField, Object>>> validateAdd(Long optid, Map<Long, Map<${Model}TypeField, Object>> context) {
        Map<Long, CheckResult<Map<${Model}TypeField, Object>>> resultMap=new HashMap<Long, CheckResult<Map<${Model}TypeField, Object>>>();
        for (Long key : context.keySet()) {
            Map<${Model}TypeField, Object> map=context.get(key);
            CheckResult<Map<${Model}TypeField, Object>> checkResult=new CheckResult<Map<${Model}TypeField, Object>>(map);
            // TODO 默认校验通过
            resultMap.put(key,checkResult);
        }
        return resultMap;
    }


    @Override
    protected Map<Long, CheckResult<Map<${Model}TypeField, Object>>> validateDel(Long optid, Map<Long, Map<${Model}TypeField, Object>> context) {
        Map<Long, CheckResult<Map<${Model}TypeField, Object>>> resultMap=new HashMap<Long, CheckResult<Map<${Model}TypeField, Object>>>();
        Map<Long, Boolean> idCheckResult = ${model}Service.get${Model}Exist(context.keySet());
        for (Long key : context.keySet()) {
            if (!idCheckResult.get(key)) {
                resultMap.put(key, new CheckResult<Map<${Model}TypeField, Object>>(BizError.getBizError(ErrorCode.ERROR_EXIST_ERROR, "${Model} " + ${Model}TypeField.${pk}.name() + " not exist.", ${Model}TypeField.${pk}.name())));
                continue;
            }
            CheckResult<Map<${Model}TypeField, Object>> checkResult=new CheckResult<Map<${Model}TypeField, Object>>(context.get(key));
            // TODO 默认校验通过
            resultMap.put(key,checkResult);
        }

        return resultMap;
    }

}
