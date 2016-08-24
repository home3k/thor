/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @version 1.0
 * @title
 */
public class QueryRequest<C extends ConditionField> extends CommonRequest {
    /**
     * 查询fields
     */
    private String[] fields;
    /**
     * 查询条件
     */
    private ConditionPair<C>[] conditions;
    /**
     * 查询选项Limit
     */
    private OptionLimit limits;
    /**
     * 查询选项Orderby
     */
    private OptionOrderby<String> optionOrderby;

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public ConditionPair<C>[] getConditions() {
        return conditions;
    }

    public void setConditions(ConditionPair<C>[] conditions) {
        this.conditions = conditions;
    }


    public OptionOrderby<String> getOptionOrderby() {
        return optionOrderby;
    }

    public void setOptionOrderby(OptionOrderby<String> optionOrderby) {
        this.optionOrderby = optionOrderby;
    }

    /**
     * 前端json只能转换到具体类，BIZ层只调用了此方法
     *
     * @return
     */
    public Option[] getOptions() {
        Option[] options = new Option[2];
        options[0] = getLimits();
        options[1] = getOptionOrderby();
        return options;
    }

    public OptionLimit getLimits() {
        return limits;
    }

    public void setLimits(OptionLimit limits) {
        this.limits = limits;
    }
}
