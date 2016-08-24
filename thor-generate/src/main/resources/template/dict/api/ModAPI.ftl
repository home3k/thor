/*
* Copyright 2014 51haoyayi.com Inc Limited.
* All rights reserved.
*/

package com.haoyayi.thor.api.dict.api;

import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.dict.dto.${Model}Type;
import com.haoyayi.thor.api.dict.dto.${Model}TypeField;

/**
 * ${desc}修改API
 *
 */
public interface ${Model}ModAPI {

    /**
     * 添加${desc}
     *
     * @param optid
     * @param addRequests
     * @return
     */
    AddResponse<${Model}Type> add${Model}(Long optid, AddRequest<${Model}TypeField>[] addRequests);

    /**
     * 修改${desc}
     *
     * @param optid
     * @param modRequests
     * @return
     */
    ModResponse<${Model}Type> mod${Model}(Long optid, ModRequest<${Model}TypeField>[] modRequests);

    /**
     * 删除${desc}
     *
     * @param optid
     * @param delRequests
     * @return
     */
    DelResponse<${Model}Type> del${Model}(Long optid, DelRequest<${Model}TypeField>[] delRequests);

}
