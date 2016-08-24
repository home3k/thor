/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.meta;


/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class FieldOption {

    private Boolean store = true;
    private Boolean expose = true;
    private Boolean newtable = false;
    private Boolean newdate = false;

    private Boolean array = false;

    private Boolean autoKey = false;

    private Boolean date = false;

    private Boolean boolType = false;

    private Boolean updateDate = false;

    private Boolean del = false;

    private Boolean refModel = false;
    
    private Boolean refMappingModel = false;

    private Boolean readonly = false;

    private Boolean dict = false;

    private Boolean refDict = false;

    private Boolean dictJoiner = false;

    private Boolean dictMask = false;

    private Boolean dentistSign = false;
    
    private Boolean anytimeNew = false;
    
    public Boolean getDate() {
        return date;
    }

    public void setDate(Boolean date) {
        this.date = date;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    public Boolean getExpose() {
        return expose;
    }

    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    public Boolean getNewtable() {
        return newtable;
    }

    public void setNewtable(Boolean newtable) {
        this.newtable = newtable;
    }

    public Boolean getNewdate() {
        return newdate;
    }

    public void setNewdate(Boolean newdate) {
        this.newdate = newdate;
    }

    public Boolean getArray() {
        return array;
    }

    public void setArray(Boolean array) {
        this.array = array;
    }

    public Boolean getAutoKey() {
        return autoKey;
    }

    public void setAutoKey(Boolean autoKey) {
        this.autoKey = autoKey;
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

    public Boolean getDel() {
        return del;
    }

    public void setDel(Boolean del) {
        this.del = del;
    }

    public Boolean getRefModel() {
        return refModel;
    }

    public void setRefModel(Boolean refModel) {
        this.refModel = refModel;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public Boolean getDict() {
        return dict;
    }

    public void setDict(Boolean dict) {
        this.dict = dict;
    }

    public Boolean getRefDict() {
        return refDict;
    }

    public void setRefDict(Boolean refDict) {
        this.refDict = refDict;
    }

    public Boolean getDictJoiner() {
        return dictJoiner;
    }

    public void setDictJoiner(Boolean dictJoiner) {
        this.dictJoiner = dictJoiner;
    }

    public Boolean getDictMask() {
        return dictMask;
    }

    public void setDictMask(Boolean dictMask) {
        this.dictMask = dictMask;
    }

	public Boolean getDentistSign() {
		return dentistSign;
	}

	public void setDentistSign(Boolean dentistSign) {
		this.dentistSign = dentistSign;
	}

	public Boolean getAnytimeNew() {
		return anytimeNew;
	}

	public void setAnytimeNew(Boolean anytimeNew) {
		this.anytimeNew = anytimeNew;
	}

	public Boolean getRefMappingModel() {
		return refMappingModel;
	}

	public void setRefMappingModel(Boolean refMappingModel) {
		this.refMappingModel = refMappingModel;
	}
}
