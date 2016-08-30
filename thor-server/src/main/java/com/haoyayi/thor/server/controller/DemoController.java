/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.server.controller;

import com.haoyayi.thor.api.*;
import com.haoyayi.thor.api.wrapper.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * @author home3k
 */
public class DemoController {

    @POST
    @Path("/demo")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public CommonResponse addSection(@Context HttpHeaders headers,AddRequestWrapper<SectionTypeField> addRequest) {
        AddResponse<SectionType> response = new AddResponse<SectionType>();
        try {
            return sectionModAPI.addSection(addRequest.getOptid(), addRequest.getAddRequests());
        } catch (Throwable throwable) {
            LOG.error("section add error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/mod")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public ModResponse<SectionType> modSection(@Context HttpHeaders headers,ModRequestWrapper<SectionTypeField> modRequest) {
        ModResponse<SectionType> response = new ModResponse<SectionType>();
        try {
            return sectionModAPI.modSection(modRequest.getOptid(), modRequest.getModRequests());
        } catch (Throwable throwable) {
            LOG.error("section mod error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @Path("/person/{id:\\d+}/")
    @DELETE
    public Response deletePerson() {
        ......
    }


    @DELETE
    @Path("/del")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public DelResponse<SectionType> delSection(@Context HttpHeaders headers, @PathParam("id") int id, @PathParam("conditions") String conditions) {
        DelResponse<SectionType> response = new DelResponse<SectionType>();
        try {
            return sectionModAPI.delSection(delRequest.getOptid(), delRequest.getDelRequests());
        } catch (Throwable throwable) {
            LOG.error("section del error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @GET
    @Path("/get")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public QueryResponse<SectionType> querySection(@Context HttpHeaders headers, QueryRequestWrapper<SectionTypeField, SectionConditionField> queryRequest) {
        QueryResponse<SectionType> response = new QueryResponse<SectionType>();
        try {
            return sectionQueryAPI.querySection(queryRequest.getOptid(), queryRequest.getQueryRequest());
        } catch (Throwable throwable) {
            LOG.error("section query error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/get/count")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public CountResponse querySection(@Context HttpHeaders headers, QueryCountRequestWrapper<SectionConditionField> queryRequest) {
        CountResponse response = new CountResponse();
        try {
            return sectionQueryAPI.querySectionCount(queryRequest.getOptid(), queryRequest.getCountRequest());
        } catch (Throwable throwable) {
            LOG.error("section query count error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

    @POST
    @Path("/get/groupby")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public QueryGroupByResponse querySection(@Context HttpHeaders headers, QueryGroupByRequestWrapper<SectionConditionField, SectionTypeField> queryRequest) {
        QueryGroupByResponse response = new QueryGroupByResponse();
        try {
            return sectionQueryAPI.querySectionGroupBy(queryRequest.getOptid(), queryRequest.getGroupByRequest());
        } catch (Throwable throwable) {
            LOG.error("section query groupby error!", throwable);
            response.setStatus(CommonResponse.API_STATUS_FAILED);
        }
        return response;
    }

}
