/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.meta;

import java.util.List;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class BoField {

    private String model;
    private String MODEL;

    private String bo;
    private String BO;
    private Boolean isArray = false;
    private Boolean newDate = false;
    private Boolean updateDate = false;
    private String date;
    private String DATE;
    private String refpk;
    private String REFPK;

    private String ignoreFields;

    private List<String> boolFields;

    private Boolean autoKey = false;
    private Boolean ignore = false;

    private Boolean boolType = false;

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getREFPK() {
        return REFPK;
    }

    public void setREFPK(String REFPK) {
        this.REFPK = REFPK;
    }

    public String getRefpk() {
        return refpk;
    }

    public void setRefpk(String refpk) {
        this.refpk = refpk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBO() {
        return BO;
    }

    public void setBO(String BO) {
        this.BO = BO;
    }

    public String getBo() {
        return bo;
    }

    public void setBo(String bo) {
        this.bo = bo;
    }

    public Boolean getIsArray() {
        return isArray;
    }

    public void setIsArray(Boolean isArray) {
        this.isArray = isArray;
    }

    public Boolean getNewDate() {
        return newDate;
    }

    public void setNewDate(Boolean newDate) {
        this.newDate = newDate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMODEL() {
        return MODEL;
    }

    public void setMODEL(String MODEL) {
        this.MODEL = MODEL;
    }

    public Boolean getAutoKey() {
        return autoKey;
    }

    public void setAutoKey(Boolean autoKey) {
        this.autoKey = autoKey;
    }

    public String getIgnoreFields() {
        return ignoreFields;
    }

    public void setIgnoreFields(String ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    public List<String> getBoolFields() {
        return boolFields;
    }

    public void setBoolFields(List<String> boolFields) {
        this.boolFields = boolFields;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public Boolean getBoolType() {
        return boolType;
    }

    public void setBoolType(Boolean boolType) {
        this.boolType = boolType;
    }

    public Boolean getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Boolean updateDate) {
        this.updateDate = updateDate;
    }
}
