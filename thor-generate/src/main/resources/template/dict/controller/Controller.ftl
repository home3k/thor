/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.controller;

import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.dict.api.*;
import com.haoyayi.thor.api.dict.dto.*;
import com.haoyayi.thor.api.wrapper.AddRequestWrapper;
import com.haoyayi.thor.api.wrapper.DelRequestWrapper;
import com.haoyayi.thor.api.wrapper.ModRequestWrapper;
import com.haoyayi.thor.api.wrapper.QueryRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * 字典 Controller expose restful API
 *
 */
@Controller
@Path("/dict/")
public class DictController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(DictController.class);

<#list models as model>

    @Autowired
    private ${model.Model}ModAPI ${model.model}ModAPI;

    @Autowired
    private ${model.Model}QueryAPI ${model.model}QueryAPI;

</#list>

<#list models as model>

    @POST
    @Path("${model.model}/add")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AddResponse<${model.Model}Type> add${model.Model}(@Context HttpHeaders headers,AddRequestWrapper<${model.Model}TypeField> addRequest) {
        AddResponse<${model.Model}Type> response = new AddResponse<${model.Model}Type>();
        try {
            return ${model.model}ModAPI.add${model.Model}(addRequest.getOptid(), addRequest.getAddRequests());
        } catch (Throwable throwable) {
            LOG.error("${model.model} add error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("${model.model}/mod")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public ModResponse<${model.Model}Type> mod${model.Model}(@Context HttpHeaders headers,ModRequestWrapper<${model.Model}TypeField> modRequest) {
        ModResponse<${model.Model}Type> response = new ModResponse<${model.Model}Type>();
        try {
            return ${model.model}ModAPI.mod${model.Model}(modRequest.getOptid(), modRequest.getModRequests());
        } catch (Throwable throwable) {
            LOG.error("${model.model} mod error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("${model.model}/del")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public DelResponse<${model.Model}Type> del${model.Model}(@Context HttpHeaders headers, DelRequestWrapper<${model.Model}TypeField> delRequest) {
        DelResponse<${model.Model}Type> response = new DelResponse<${model.Model}Type>();
        try {
            return ${model.model}ModAPI.del${model.Model}(delRequest.getOptid(), delRequest.getDelRequests());
        } catch (Throwable throwable) {
            LOG.error("${model.model} del error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("${model.model}/get")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public QueryResponse<${model.Model}Type> query${model.Model}(@Context HttpHeaders headers, QueryRequestWrapper<${model.Model}TypeField, ${model.Model}ConditionField> queryRequest) {
        QueryResponse<${model.Model}Type> response = new QueryResponse<${model.Model}Type>();
        try {
            return ${model.model}QueryAPI.query${model.Model}(queryRequest.getOptid(), queryRequest.getQueryRequest());
        } catch (Throwable throwable) {
            LOG.error("${model.model} query error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

</#list>


}
