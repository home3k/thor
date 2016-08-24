/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.impl.${model};

import com.haoyayi.thor.api.QueryGroupByRequest;
import com.haoyayi.thor.api.QueryGroupByResponse;
import com.haoyayi.thor.api.QueryRequest;
import com.haoyayi.thor.api.QueryResponse;
import com.haoyayi.thor.api.CountRequest;
import com.haoyayi.thor.api.CountResponse;
import com.haoyayi.thor.api.${model}.api.${Model}QueryAPI;
import com.haoyayi.thor.api.${model}.dto.${Model}ConditionField;
import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;
import com.haoyayi.thor.biz.${model}.${Model}QueryBiz;
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

    @Override
    public CountResponse query${Model}Count(Long optid, CountRequest<${Model}ConditionField> countRequest) {
        return this.query(optid, countRequest);
    }

    @Override
    public QueryGroupByResponse query${Model}GroupBy(Long optid, QueryGroupByRequest<${Model}ConditionField, ${Model}TypeField> queryRequests) {
        return this.query(optid, queryRequests);
    }

}
