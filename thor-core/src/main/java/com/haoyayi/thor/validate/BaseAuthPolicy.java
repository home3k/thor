/*
 * Copyright 2014-2020 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.validate;

import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.api.wrapper.GenericRequestWrapper;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.context.InvokeContextDict;
import com.haoyayi.thor.processor.ProcessorContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class BaseAuthPolicy {

    @Autowired
    protected ProcessorContext processorContext;

    protected String getController(ProceedingJoinPoint pjp) {
        return pjp.getSignature().getDeclaringType().getSimpleName();
    }

    protected ModelType getModelType(ProceedingJoinPoint pjp) {
        String controller = getController(pjp);
        String model = CamelUtils.lowerFirst(controller.substring(0, controller.indexOf("Controller")));
        return ModelType.valueOf(model);
    }

    protected TokenAuthItem getTokenAuthItem(ProceedingJoinPoint pjp) throws Exception {

        GenericRequestWrapper requestWrapper = (GenericRequestWrapper) pjp.getArgs()[1];

        TokenAuthItem tokenAuthItem = new TokenAuthItem();
        tokenAuthItem.setOptid(requestWrapper.getOptid());
        String token = requestWrapper.getToken();
        if (StringUtils.isBlank(token)) {
            token = getTokenWithHeaders((HttpHeaders) pjp.getArgs()[0]);
        }
        tokenAuthItem.setToken(token);
        return tokenAuthItem;
    }

    protected String getTokenWithHeaders(HttpHeaders headers) {
        MultivaluedMap<String, String> headerMap = headers.getRequestHeaders();
        return headerMap.getFirst(InvokeContextDict.TOKEN.getValue());
    }


    protected class TokenAuthItem {

        private Long optid;

        private String token;

        public Long getOptid() {
            return optid;
        }

        public void setOptid(Long optid) {
            this.optid = optid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }


}
