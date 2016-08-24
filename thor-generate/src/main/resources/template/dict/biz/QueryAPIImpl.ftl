/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.impl.dict;

import com.haoyayi.thor.api.QueryRequest;
import com.haoyayi.thor.api.QueryResponse;
import com.haoyayi.thor.api.CountRequest;
import com.haoyayi.thor.api.CountResponse;
import com.haoyayi.thor.api.dict.api.${Model}QueryAPI;
import com.haoyayi.thor.api.dict.dto.${Model}ConditionField;
import com.haoyayi.thor.api.dict.dto.${Model}Type;
import com.haoyayi.thor.api.dict.dto.${Model}TypeField;
import com.haoyayi.thor.biz.dict.${Model}QueryBiz;
import com.haoyayi.thor.impl.base.AbstractQueryAPIImpl;
import com.haoyayi.thor.query.QueryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${desc}查询接口实现
 */
@Service
public class ${Model}QueryAPIImpl extends AbstractQueryAPIImpl<${Model}Type,${Model}TypeField, ${Model}ConditionField> implements ${Model}QueryAPI {

    @Autowired
    private ${Model}QueryBiz ${model}QueryBiz;

    @Override
    protected QueryFacade<${Model}Type, ${Model}TypeField, ${Model}ConditionField> getQueryFacade() {
        return ${model}QueryBiz;
    }

    @Override
    public QueryResponse<${Model}Type> query${Model}(Long optid, QueryRequest<${Model}ConditionField> queryRequests) {
        return this.query(optid, queryRequests);
    }

}
