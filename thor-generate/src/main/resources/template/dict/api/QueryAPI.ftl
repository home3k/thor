/*
* Copyright 2014 51haoyayi.com Inc Limited.
* All rights reserved.
*/

package com.haoyayi.thor.api.dict.api;

import com.haoyayi.thor.api.QueryRequest;
import com.haoyayi.thor.api.QueryResponse;
import com.haoyayi.thor.api.dict.dto.${Model}ConditionField;
import com.haoyayi.thor.api.dict.dto.${Model}Type;
import com.haoyayi.thor.api.dict.dto.${Model}TypeField;

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

}
