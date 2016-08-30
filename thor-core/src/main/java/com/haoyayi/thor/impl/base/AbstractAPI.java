/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.impl.base;


import com.haoyayi.thor.api.CommonRequest;
import com.haoyayi.thor.api.CommonResponse;
import com.haoyayi.thor.api.Error;
import com.haoyayi.thor.api.ErrorCode;

/**
 * @author home3k (sunkai@51haoyayia.com)
 * @version 1.0
 * @description API实现的顶层抽象类
 */
public abstract class AbstractAPI {

    /**
     * build params error
     *
     * @param response
     */
    private void buildError(CommonResponse response) {
        Error error = new Error();
        error.setErrorCode(ErrorCode.ERROR_PARAM_COMMON_ERROR);
        error.setErrorMessage("API params parameter is error");
        response.setError(error);
        response.setStatus(CommonResponse.API_STATUS_FAILED);

    }

    /**
     * 基本参数验证
     *
     * @param response
     * @param params
     * @return true 验证成功，false 验证失败
     */
    protected boolean checkParams(CommonResponse response, CommonRequest... params) {

        // parameters is null
        if (params == null) {
            buildError(response);
            return false;
        } else {
            // 参数为空
            if (params.length == 0) {
                buildError(response);
                return false;
            }
            for (CommonRequest request : params) {
                // 判断单独的request对象是否为空
                if (request == null) {
                    buildError(response);
                    return false;
                }
            }
        }
        return true;
    }

}
