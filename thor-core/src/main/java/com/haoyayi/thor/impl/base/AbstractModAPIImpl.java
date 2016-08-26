/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.impl.base;

import com.haoyayi.thor.ModelAware;
import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.Error;
import com.haoyayi.thor.biz.BizCommandProcessor;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.context.BizContextDict;
import com.haoyayi.thor.context.BizContextHolder;
import com.haoyayi.thor.exception.BizException;
import com.haoyayi.thor.processor.ColumnProcessor;
import com.haoyayi.thor.processor.ProcessorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 * @description 修改操作的基本抽象类
 */
public abstract class AbstractModAPIImpl<T extends BaseType, V extends BaseTypeField> extends
        AbstractAPI implements InitializingBean, ModelAware {

    private static Logger LOG = LoggerFactory.getLogger(AbstractModAPIImpl.class);

    @Autowired
    private ProcessorContext processorContext;

    private ColumnProcessor<V> columnProcessor;

    @Override
    public void afterPropertiesSet() throws Exception {

        // 初始化columnProcessor
        columnProcessor = processorContext.getConverter(getModelType());
    }

    /**
     * 获得processor
     *
     * @return
     */
    protected abstract BizCommandProcessor<T, V> getBizProcessor();

    /**
     * Command执行入口
     *
     * @param optid
     * @param requests
     * @param action
     * @return
     */
    protected CommonResponse command(Long optid, CommonRequest[] requests, OpType action) {
        CommonResponse response = new CommonResponse();
        try {
            // 1. 参数验证
            if (!checkParams(optid, response, requests)) {
                return response;
            }

            // 2. 转换成request
            Map<Long, Map<V, Object>> context = convertRequest(requests, action);

            // 3. 进行biz处理
            Map<Long, CheckResult<T>> result = process(optid, context, action);

            // 4. 处理返回结果
            return convertResponse(result, action);
        } catch (Throwable e) {
            LOG.error("Command error.", e);
            throw new BizException("Command error." + e.getMessage());
        }
    }

    protected Map<Long, CheckResult<T>> process(Long optid, Map<Long, Map<V, Object>> context, OpType action) {
        switch (action) {
            case MOD:
                return getBizProcessor().mod(optid, context);
            case ADD:
                return getBizProcessor().add(optid, context);
            case DEL:
                return getBizProcessor().del(optid, context);
            default:
                throw new BizException("The action type invalid: " + action);
        }
    }

    /**
     * 转换为DTO
     *
     * @param response
     * @param action
     * @return
     * @throws Exception
     */
    public CommonResponse convertResponse(Map<Long, CheckResult<T>> response, OpType action) throws Exception {
        switch (action) {
            case MOD:
                return convertModResponse(response);
            case ADD:
                return convertAddResponse(response);
            case DEL:
                return convertDelResponse(response);
            default:
                throw new BizException("The action type invalid: " + action);
        }
    }

    /**
     * 转换Mod的返回DTO
     *
     * @param response
     * @return
     * @throws Exception
     */
    protected ModResponse<T> convertModResponse(Map<Long, CheckResult<T>> response) throws Exception {
        ModResponse<T> result = new ModResponse<T>();

        Map<Long, T> data = new HashMap<Long, T>();
        Map<Long, Map<String, Error>> errorInfo = new HashMap<Long, Map<String, Error>>();
        // 遍历返回的结果
        for (Long key : response.keySet()) {
            CheckResult<T> checkResult = response.get(key);
            // 错误信息的处理
            if (checkResult.isErrorResult()) {
                BizError bizError = checkResult.getError();
                Map<String, Error> errorMap = new HashMap<String, Error>();
                Error error = new Error();
                error.setErrorCode(bizError.getErrorCode());
                error.setErrorMessage(bizError.getErrorMessage());
                errorMap.put(checkResult.getError().getModelField(), error);
                errorInfo.put(key, errorMap);
            } else {
                // 设置Data数据
                data.put(key, checkResult.getData());
            }
        }
        result.setData(data);
        result.setErrorInfo(errorInfo);

        // 设置状态数据
        if (data.keySet().size() == response.size()) {
            result.setStatus(CommonResponse.API_STATUS_OK);
        } else if (data.keySet().size() == 0) {
            result.setStatus(CommonResponse.API_STATUS_FAILED);
        } else {
            result.setStatus(CommonResponse.API_STATUS_PARTLY_OK);
        }
        return result;
    }

    /**
     * 处理Del的DTO
     *
     * @param response
     * @return
     * @throws Exception
     */
    protected DelResponse<T> convertDelResponse(Map<Long, CheckResult<T>> response) throws Exception {
        DelResponse<T> result = new DelResponse<T>();

        Map<Long, T> data = new HashMap<Long, T>();
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
        result.setData(data);
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

    /**
     * 处理Add操作的DTO
     *
     * @param response
     * @return
     * @throws Exception
     */
    protected AddResponse<T> convertAddResponse(Map<Long, CheckResult<T>> response) throws Exception {
        AddResponse<T> result = new AddResponse<T>();

        Map<Integer, T> data = new HashMap<Integer, T>();
        Map<Integer, Map<String, Error>> errorInfo = new HashMap<Integer, Map<String, Error>>();
        // 遍历
        for (Long key : response.keySet()) {
            CheckResult<T> checkResult = response.get(key);
            if (checkResult.isErrorResult()) {
                // 处理错误数据
                BizError bizError = checkResult.getError();
                Map<String, Error> errorMap = new HashMap<String, Error>();
                Error error = new Error();
                error.setErrorCode(bizError.getErrorCode());
                error.setErrorMessage(bizError.getErrorMessage());
                errorMap.put(checkResult.getError().getModelField(), error);
                errorInfo.put(key.intValue(), errorMap);
            } else {
                // 处理正确数据
                data.put(key.intValue(), checkResult.getData());
            }
        }
        result.setData(data);
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

    /**
     * 转换请求DTO
     *
     * @param requests
     * @param action
     * @return
     */
    protected Map<Long, Map<V, Object>> convertRequest(CommonRequest[] requests, OpType action) {
        switch (action) {
            case MOD:
                return convertModRequest((ModRequest<V>[]) requests);
            case ADD:
                return convertAddRequest((AddRequest<V>[]) requests);
            case DEL:
                return convertDelRequest((DelRequest<V>[]) requests);
            default:
                throw new IllegalStateException("");
        }
    }

    /**
     * 转换AddRequest.
     *
     * @param requests
     * @return
     */
    protected Map<Long, Map<V, Object>> convertAddRequest(AddRequest<V>[] requests) {
        Map<Long, Map<V, Object>> context = new LinkedHashMap<Long, Map<V, Object>>();
        for (int i = 0; i < requests.length; i++) {
            AddRequest<V> request = requests[i];
            context.put(Long.valueOf(i), request.getFields());
        }
        return context;
    }

    /**
     * 转换DelRequest.
     *
     * @param requests
     * @return
     */
    protected Map<Long, Map<V, Object>> convertDelRequest(DelRequest<V>[] requests) {
        Map<Long, Map<V, Object>> context = new HashMap<Long, Map<V, Object>>();
        Map<Long, V[]> resFields = new HashMap<Long, V[]>();
        for (int i = 0; i < requests.length; i++) {
            DelRequest<V> request = requests[i];
            // del时,context的data传入Model的keyid
            Map<V, Object> requestMap = new HashMap<V, Object>();
            requestMap.put(columnProcessor.getPkField(), request.getId());
            context.put(request.getId(), requestMap);
            resFields.put(request.getId(), request.getResFields());
        }
        // 设置res_fields字段。
        BizContextHolder.getInstance().setBizContext(BizContextDict.RES_FIELDS, resFields);
        return context;
    }

    /**
     * 转换ModRequest.
     *
     * @param requests
     * @return
     */
    protected Map<Long, Map<V, Object>> convertModRequest(ModRequest<V>[] requests) {
        Map<Long, Map<V, Object>> context = new HashMap<Long, Map<V, Object>>();
        Map<Long, V[]> resFields = new HashMap<Long, V[]>();
        for (int i = 0; i < requests.length; i++) {
            ModRequest<V> request = requests[i];
            context.put(request.getId(), request.getFields());
            resFields.put(request.getId(), request.getResFields());
        }
        BizContextHolder.getInstance().setBizContext(BizContextDict.RES_FIELDS, resFields);
        return context;
    }

}
