/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoyayi.thor.api.CommonResponse;
import com.haoyayi.thor.api.Error;
import com.haoyayi.thor.api.ErrorCode;
import com.haoyayi.thor.api.RequestExtraDict;
import com.haoyayi.thor.api.wrapper.AbstractWrapper;
import com.haoyayi.thor.context.InvokeContextDict;
import com.haoyayi.thor.context.InvokeContextHolder;
import com.haoyayi.thor.exception.AuthException;
import com.haoyayi.thor.utils.CostTime;
import com.haoyayi.thor.utils.NetworkUtils;


/**
 * 收到请求的AOP
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class RpcContextInteceptor {

    /**
     * Process LOGGER
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(RpcContextInteceptor.class);

    final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss,SSS");

    ObjectMapper mapper = new ObjectMapper();

    protected void clear() {
        InvokeContextHolder.getInstance().clear();
    }


    /**
     * Invoke method.
     *
     * @param pjp
     * @return
     * @throws Throwable Object
     * @author sunkai
     */
    public Object rpcInvoke(ProceedingJoinPoint pjp) throws Throwable {

        Object result = null;
        Object[] params = pjp.getArgs();
        String paramStr = mapper.writeValueAsString(params).replaceAll(" ", "_");

        // Get http headers
        HttpHeaders headers = (HttpHeaders) params[0];

        String clazz = pjp.getTarget().getClass().getSimpleName();

        String methodName = pjp.getSignature().getName();

        int status = CommonResponse.API_STATUS_OK;

        CostTime costtime = new CostTime();
        costtime.start();
        Date startTime = new Date();

        try {
            //process join point
            result = pjp.proceed(params);
            status = ((CommonResponse) result).getStatus();
        } catch (Throwable t) {
            //exception found, set status & throw it.
            try {
                return wrapperException(t, pjp);
            } catch (Throwable th) {
                throw th;
            }
        } finally {
            //dump log. TODO
        }
        return result;

    }

    private Object wrapperException(Throwable t, ProceedingJoinPoint pjp) throws Exception {
        Method method = pjp.getSignature().getDeclaringType().getMethod(
                pjp.getSignature().getName(),
                new Class[]{HttpHeaders.class, pjp.getArgs()[1].getClass()});
        Type returnType = method.getReturnType();
        Object response = ((Class) returnType).newInstance();
        Method setStatus = response.getClass().getMethod("setStatus", Integer.class);

        setStatus.invoke(response, CommonResponse.API_STATUS_FAILED);

        return response;
    }


}