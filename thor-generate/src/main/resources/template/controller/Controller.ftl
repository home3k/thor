/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.controller;

import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.${model}.api.*;
import com.haoyayi.thor.api.${model}.dto.*;
import com.haoyayi.thor.api.wrapper.AddRequestWrapper;
import com.haoyayi.thor.api.wrapper.DelRequestWrapper;
import com.haoyayi.thor.api.wrapper.ModRequestWrapper;
import com.haoyayi.thor.api.wrapper.QueryRequestWrapper;
import com.haoyayi.thor.api.wrapper.QueryCountRequestWrapper;
import com.haoyayi.thor.api.wrapper.QueryGroupByRequestWrapper;
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
 * ${desc} Controller expose restful API
 *
 */
@Controller
@Path("/${model}/")
public class ${Model}Controller extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(${Model}Controller.class);

    @Autowired
    private ${Model}ModAPI ${model}ModAPI;

    @Autowired
    private ${Model}QueryAPI ${model}QueryAPI;

    @POST
    @Path("/add")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AddResponse<${Model}Type> add${Model}(@Context HttpHeaders headers,AddRequestWrapper<${Model}TypeField> addRequest) {
        AddResponse<${Model}Type> response = new AddResponse<${Model}Type>();
        try {
            return ${model}ModAPI.add${Model}(addRequest.getOptid(), addRequest.getAddRequests());
        } catch (Throwable throwable) {
            LOG.error("${model} add error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/mod")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public ModResponse<${Model}Type> mod${Model}(@Context HttpHeaders headers,ModRequestWrapper<${Model}TypeField> modRequest) {
        ModResponse<${Model}Type> response = new ModResponse<${Model}Type>();
        try {
            return ${model}ModAPI.mod${Model}(modRequest.getOptid(), modRequest.getModRequests());
        } catch (Throwable throwable) {
            LOG.error("${model} mod error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/del")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public DelResponse<${Model}Type> del${Model}(@Context HttpHeaders headers, DelRequestWrapper<${Model}TypeField> delRequest) {
        DelResponse<${Model}Type> response = new DelResponse<${Model}Type>();
        try {
            return ${model}ModAPI.del${Model}(delRequest.getOptid(), delRequest.getDelRequests());
        } catch (Throwable throwable) {
            LOG.error("${model} del error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/get")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public QueryResponse<${Model}Type> query${Model}(@Context HttpHeaders headers, QueryRequestWrapper<${Model}TypeField, ${Model}ConditionField> queryRequest) {
        QueryResponse<${Model}Type> response = new QueryResponse<${Model}Type>();
        try {
            return ${model}QueryAPI.query${Model}(queryRequest.getOptid(), queryRequest.getQueryRequest());
        } catch (Throwable throwable) {
            LOG.error("${model} query error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/get/count")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public CountResponse query${Model}(@Context HttpHeaders headers, QueryCountRequestWrapper<${Model}ConditionField> queryRequest) {
        CountResponse response = new CountResponse();
        try {
            return ${model}QueryAPI.query${Model}Count(queryRequest.getOptid(), queryRequest.getCountRequest());
        } catch (Throwable throwable) {
            LOG.error("${model} query count error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/get/groupby")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public QueryGroupByResponse query${Model}(@Context HttpHeaders headers, QueryGroupByRequestWrapper<${Model}ConditionField, ${Model}TypeField> queryRequest) {
        QueryGroupByResponse response = new QueryGroupByResponse();
        try {
            return ${model}QueryAPI.query${Model}GroupBy(queryRequest.getOptid(), queryRequest.getGroupByRequest());
        } catch (Throwable throwable) {
            LOG.error("${model} query groupby error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

}
