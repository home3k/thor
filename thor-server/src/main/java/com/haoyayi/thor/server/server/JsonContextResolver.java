/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.server;

import java.text.SimpleDateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 序列化配置:
 * 1. 日期格式序列化
 * 2. null字段忽略
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
@Component
@Provider
public class JsonContextResolver implements ContextResolver<ObjectMapper> {

    final ObjectMapper mapper = (new ObjectMapper()).setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);

    {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
