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

    public static final String COOKIE_CHANNEL = "channel";
    public static final String COOKIE_SOURCE = "source";
    public static final String COOKIE_SECTION = "section";

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
        String paramStr = mapper.writeValueAsString(params).replaceAll(" ","_");

        // Get http headers
        HttpHeaders headers = (HttpHeaders) params[0];

        if (! (params[1] instanceof AbstractWrapper)) {
	        	String methodName = pjp.getSignature().getName();
	        	LOGGER.info("methodName:" + methodName + ", params:" + params[1]);
	        	return pjp.proceed(params);
        }
        AbstractWrapper wrapper = (AbstractWrapper) params[1];

        String openid = null, sectionid = null;
        if (wrapper != null) {
            if (wrapper.getExtra() != null) {
                openid = (String)wrapper.getExtra().get(RequestExtraDict.openid);
                sectionid = (String)wrapper.getExtra().get(RequestExtraDict.sectionId);
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.OPTID, wrapper.getOptid());
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.OPENID, openid);
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SECTIONID, sectionid);
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.LOGINROLE, wrapper.getExtra().get(RequestExtraDict.loginRole));
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.LOGINNAME, wrapper.getExtra().get(RequestExtraDict.loginName));
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.LOGINDENTISTID, wrapper.getExtra().get(RequestExtraDict.loginDentistId));
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.MERGERULE, wrapper.getExtra().get(RequestExtraDict.mergeRule));
                InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.UNIONID, wrapper.getExtra().get(RequestExtraDict.unionId));
            }
            if (StringUtils.isNotEmpty(wrapper.getVersion())) {
            	InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.VERSION, wrapper.getVersion());
            }
        }

        MultivaluedMap<String, String> headerMap = headers.getRequestHeaders();


        Map<String, Cookie> cookies = headers.getCookies();

        if (cookies.containsKey(COOKIE_CHANNEL)) {
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.CHANNEL, cookies.get(COOKIE_CHANNEL).getValue());
        } else {
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.CHANNEL, headerMap.getFirst(InvokeContextDict.CHANNEL.getValue()));
        }

        if (cookies.containsKey(COOKIE_SOURCE)) {
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCE, cookies.get(COOKIE_SOURCE).getValue());
        } else {
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCE, headerMap.getFirst(InvokeContextDict.SOURCE.getValue()));
        }

        InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.TICKETID, headerMap.getFirst(InvokeContextDict.TICKETID.getValue()));
        InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.LOGID, headerMap.getFirst(InvokeContextDict.LOGID.getValue()));
        InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.FROMMODULE, headerMap.getFirst(InvokeContextDict.FROMMODULE.getValue()));
        if (StringUtils.isNotEmpty(headerMap.getFirst(InvokeContextDict.SOURCEIP.getValue()))) {
        	InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCEIP, headerMap.getFirst(InvokeContextDict.SOURCEIP.getValue()));
        } else {
        	InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCEIP, "0.0.0.1");
        }

        if (StringUtils.isNotBlank(sectionid) && !sectionid.equals("null")) {
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.CHANNEL, "Section");
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCE, sectionid);
        }

        if (StringUtils.isNotBlank(openid) && !openid.equals("null")) {
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.CHANNEL, "Wechat");
            InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCE, "-");
        }
        
        // 如果header里有， 按照header的设置
        if(StringUtils.isNotBlank(headerMap.getFirst(InvokeContextDict.CHANNEL.getValue()))) {
        		InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.CHANNEL, headerMap.getFirst(InvokeContextDict.CHANNEL.getValue()));
        }
        if(StringUtils.isNotBlank(headerMap.getFirst(InvokeContextDict.SOURCE.getValue()))) {
        	  	InvokeContextHolder.getInstance().setBizContext(InvokeContextDict.SOURCE, headerMap.getFirst(InvokeContextDict.SOURCE.getValue()));
        }

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
            //dump log.
            dumpInvokeLog(clazz, methodName, paramStr, costtime, startTime, status);
        }
        return result;

    }

    private Object wrapperException(Throwable t, ProceedingJoinPoint pjp) throws Exception {
        Method method = pjp.getSignature().getDeclaringType().getMethod(
                pjp.getSignature().getName(),
                new Class[] { HttpHeaders.class, pjp.getArgs()[1].getClass() });
        Type returnType = method.getReturnType();
        Object response = ((Class) returnType).newInstance();
        Method setStatus=response.getClass().getMethod("setStatus", Integer.class);

        setStatus.invoke(response, CommonResponse.API_STATUS_FAILED);

        if (t instanceof AuthException) {
            com.haoyayi.thor.api.Error error = new Error();
            error.setErrorCode(ErrorCode.ERROR_AUTH_COMMON_ERROR);
            error.setErrorMessage("Auth Error!");
            Method setError=response.getClass().getMethod("setError", Error.class);
            setError.invoke(response, error);

        }
        return response;
    }

    /**
     * DUMP LOG
     * <p/>
     * service api logid ticketid sourceip from_module channel source current_ip current_module params status starttime endtime cost
     * <p/>
     *
     * @param clazz
     * @param methodName
     * @param params
     * @param costTime
     * @param startTime
     * @param status     void
     * @author sunkai
     */
    private void dumpInvokeLog(String clazz, String methodName,
                               String params, CostTime costTime, Date startTime, int status) {
        try {
            LOGGER.info("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",
                    clazz,
                    methodName,
                    InvokeContextHolder.getInstance().getBizContextAsString(InvokeContextDict.LOGID),
                    InvokeContextHolder.getInstance().getBizContextAsString(InvokeContextDict.TICKETID),
                    InvokeContextHolder.getInstance().getBizContextAsString(InvokeContextDict.SOURCEIP),
                    InvokeContextHolder.getInstance().getBizContextAsString(InvokeContextDict.FROMMODULE),
                    InvokeContextHolder.getInstance().getBizContextAsString(InvokeContextDict.CHANNEL),
                    InvokeContextHolder.getInstance().getBizContextAsString(InvokeContextDict.SOURCE),
                    NetworkUtils.getLocalAddress(),
                    InvokeContextHolder.CURRENT_MODULDE,
                    params,
                    status,
                    FORMAT.format(startTime),
                    FORMAT.format(new Date()),
                    costTime.cost()
            );
        } catch (Exception e) {

        } finally {
          clear();
        }
    }

}