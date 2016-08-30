/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.impl.base;

import com.google.common.collect.ObjectArrays;
import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.Error;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.exception.BizException;
import com.haoyayi.thor.query.QueryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 查询操作实现 抽象类
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractQueryAPIImpl<T extends BaseType, V extends BaseTypeField,
        R extends ConditionField> extends
        AbstractAPI {

    final static Logger LOG = LoggerFactory.getLogger(AbstractQueryAPIImpl.class);

    /**
     * 获得QueryFacade
     *
     * @return
     */
    protected abstract QueryFacade<T, V, R> getQueryFacade();

    /**
     * 查询Count处理主流程
     *
     * @param queryRequest
     * @return
     */
    protected CountResponse query(CountRequest<R> queryRequest) {
        CountResponse response = new CountResponse();
        try {
            // 1. 参数验证
            if (!checkParams(response, queryRequest)) {
                return response;
            }

            // 2. 进行build处理
            CheckResult<Long> result = getQueryFacade().query(queryRequest.getConditions());

            // 3. 处理返回结果
            return convertCountResponse(result);
        } catch (Throwable e) {
            LOG.error("Command error.", e);
            throw new BizException("Command error." + e.getMessage());
        }
    }

    /**
     * 查询Group by 处理流程
     *
     * @param queryRequest
     * @return
     */
    protected QueryGroupByResponse query(QueryGroupByRequest<R, V> queryRequest) {
        QueryGroupByResponse response = new QueryGroupByResponse();
        try {
            // 1. 参数验证
            if (!checkParams(response, queryRequest)) {
                return response;
            }

            // 2. 进行build处理
            CheckResult<List<Map<String, Object>>> result = getQueryFacade().query(queryRequest.getConditions(), queryRequest.getGroupByFields(), queryRequest.getGroupFuncMap());

            // 3. 处理返回结果
            return convertGroupByResponse(result);
        } catch (Throwable e) {
            LOG.error("Command error.", e);
            throw new BizException("Command error." + e.getMessage());
        }
    }


    /**
     * 查询处理主流程
     *
     * @param queryRequest
     * @return
     */
    protected QueryResponse<T> query(QueryRequest<R> queryRequest) {
        QueryResponse<T> response = new QueryResponse<T>();
        try {
            // 1. 参数验证
            if (!checkParams(response, queryRequest)) {
                return response;
            }

            // 2. 进行build处理
            Map<Long, CheckResult<T>> result = getQueryFacade().query(queryRequest.getConditions(),
                    queryRequest.getOptions(), new HashSet<String>(Arrays.asList(queryRequest.getFields())));

            // 3. 处理返回结果
            return convertResponse(result);
        } catch (Throwable e) {
            LOG.error("Command error.", e);
            throw new BizException("Command error." + e.getMessage());
        }
    }


    private CountResponse convertCountResponse(CheckResult<Long> response) {
        CountResponse result = new CountResponse();

        if (response.isErrorResult()) {
            result.setError(new Error(response.getError().getErrorCode(), response.getError().getErrorMessage()));
            result.setStatus(CommonResponse.API_STATUS_FAILED);
        } else {
            result.setData(response.getData());
            result.setStatus(CommonResponse.API_STATUS_OK);
        }
        return result;
    }

    private QueryGroupByResponse convertGroupByResponse(CheckResult<List<Map<String, Object>>> response) {
        QueryGroupByResponse result = new QueryGroupByResponse();

        if (response.isErrorResult()) {
            result.setError(new Error(response.getError().getErrorCode(), response.getError().getErrorMessage()));
            result.setStatus(CommonResponse.API_STATUS_FAILED);
        } else {
            result.setData(response.getData().toArray(ObjectArrays.newArray(Map.class, 0)));
            result.setStatus(CommonResponse.API_STATUS_OK);
        }
        return result;

    }

    /**
     * 转换为response对象
     *
     * @param response
     * @return
     */
    private QueryResponse<T> convertResponse(Map<Long, CheckResult<T>> response) {
        QueryResponse<T> result = new QueryResponse<T>();

        Map<Long, T> data = new LinkedHashMap<Long, T>();
        Map<Long, Error> errorInfo = new HashMap<Long, Error>();

        for (Long key : response.keySet()) {
            CheckResult<T> checkResult = response.get(key);
            if (checkResult.isErrorResult()) {
                // 处理错误数据
                BizError bizError = checkResult.getError();
                Error error = new Error();
                error.setErrorCode(bizError.getErrorCode());
                error.setErrorMessage(bizError.getErrorMessage());
                errorInfo.put(key, error);
            } else {
                data.put(key, checkResult.getData());
            }
        }
        List<T> res = new LinkedList<T>();
        for (T item : data.values()) {
            res.add(item);
        }
        result.setData(res);
        result.setErrorInfo(errorInfo);
        // 设置状态
        if (data.keySet().size() == response.size()) {
            result.setStatus(CommonResponse.API_STATUS_OK);
        } else if (data.keySet().size() == 0) {
            result.setStatus(CommonResponse.API_STATUS_FAILED);
        } else {
            result.setStatus(CommonResponse.API_STATUS_PARTLY_OK);
        }
        return result;
    }

}
