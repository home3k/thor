/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.common;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class BizError {

    private Integer errorCode;

    private String errorMessage;

    private String modelField = null;

    public BizError() {

    }

    public BizError(Integer code) {
        super();
        this.errorCode = code;
    }


    public BizError(Integer code, String msg) {
        super();
        this.errorCode = code;
        this.errorMessage = msg;
    }

    public BizError(Integer code, String msg, String modelField) {
        super();
        this.errorCode = code;
        this.errorMessage = msg;
        this.modelField = modelField;
    }

    public static BizError getBizError(Integer code, String msg, String modelField) {
        return new BizError(code, msg, modelField);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getModelField() {
        return modelField;
    }

    public void setModelField(String modelField) {
        this.modelField = modelField;
    }
}
