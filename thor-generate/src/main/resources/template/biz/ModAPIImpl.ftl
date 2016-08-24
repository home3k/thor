/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.impl.${model};

import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.${model}.api.${Model}ModAPI;
import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;
import com.haoyayi.thor.biz.BizCommandProcessor;
import com.haoyayi.thor.biz.${model}.${Model}Biz;
import com.haoyayi.thor.impl.base.AbstractModAPIImpl;
import com.haoyayi.thor.impl.base.OpType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${desc}修改接口实现
 */
@Service
public class ${Model}ModAPIImpl extends AbstractModAPIImpl<${Model}Type, ${Model}TypeField> implements ${Model}ModAPI {

    @Autowired
    private ${Model}Biz ${model}Biz;

    protected ModelType getModelType() {
        return ModelType.${model};
    }

    @Override
    public AddResponse<${Model}Type> add${Model}(Long optid, AddRequest<${Model}TypeField>[] addRequests) {
        return (AddResponse<${Model}Type>) command(optid, addRequests, OpType.ADD);
    }

    @Override
    public ModResponse<${Model}Type> mod${Model}(Long optid, ModRequest<${Model}TypeField>[] modRequests) {
        return (ModResponse<${Model}Type>) command(optid, modRequests, OpType.MOD);
    }

    @Override
    public DelResponse<${Model}Type> del${Model}(Long optid, DelRequest<${Model}TypeField>[] delRequests) {
        return (DelResponse<${Model}Type>) command(optid, delRequests, OpType.DEL);
    }

    @Override
    protected BizCommandProcessor<${Model}Type, ${Model}TypeField> getBizProcessor() {
        return ${model}Biz;
    }
}
