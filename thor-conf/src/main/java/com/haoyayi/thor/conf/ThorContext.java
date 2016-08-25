/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.conf;

/**
 * @author home3k
 */
public class ThorContext {

    /**
     * the model yaml path. default classpath: model/
     */
    private String modelPath = "model";

    /**
     * the dict yaml path. default classpath: dict/
     */
    private String dictPath = "dict";

    /**
     * the generate code of api class file.
     */
    private String apiPath;

    /**
     * the generate code of biz class file.
     */
    private String bizPath;

    /**
     * the generate code of controller class file.
     */
    private String controllerPath;

    /**
     * the generate code of dal class file.
     */
    private String dalPath;

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getDictPath() {
        return dictPath;
    }

    public void setDictPath(String dictPath) {
        this.dictPath = dictPath;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getBizPath() {
        return bizPath;
    }

    public void setBizPath(String bizPath) {
        this.bizPath = bizPath;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public String getDalPath() {
        return dalPath;
    }

    public void setDalPath(String dalPath) {
        this.dalPath = dalPath;
    }
}
