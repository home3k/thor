/*
* Copyright 2014 51haoyayi.com Inc Limited.
* All rights reserved.
*/

package com.haoyayi.thor.api.${model}.api;

import com.haoyayi.thor.api.QueryGroupByRequest;
import com.haoyayi.thor.api.QueryGroupByResponse;
import com.haoyayi.thor.api.CountRequest;
import com.haoyayi.thor.api.CountResponse;
import com.haoyayi.thor.api.QueryRequest;
import com.haoyayi.thor.api.QueryResponse;
import com.haoyayi.thor.api.${model}.dto.${Model}ConditionField;
import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;

/**
 * ${desc}查询API
 *
 */
public interface ${Model}QueryAPI {

    /**
     * 查询${desc}
     *
     * @param optid
     * @param queryRequests
     * @return
     */
    QueryResponse<${Model}Type> query${Model}(Long optid, QueryRequest<${Model}ConditionField> queryRequests);

    /**
     * 查询${desc}数量
     * @param optid
     * @param countRequest
     * @return
     */
    CountResponse query${Model}Count(Long optid, CountRequest<${Model}ConditionField> countRequest);

    /**
     * 查询${desc}group by
     * @param optid
     * @param queryRequests
     * @return
     */
    QueryGroupByResponse query${Model}GroupBy(Long optid, QueryGroupByRequest<${Model}ConditionField, ${Model}TypeField> queryRequests);

}
