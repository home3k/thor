/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context;

/**
 * @author home3k (sunkai@51haoyayi.com)
 * @title
 * @description 业务Context字典
 */
public enum BizContextDict {

    // 操作人id
    OPTID("optid"),

    // 返回的字段列表
    RES_FIELDS("res_fields"),

    OP_MODEL("model"),

    OP_ACTION("action");

    private final String value;

    public String getValue() {
        return value;
    }

    BizContextDict(String value) {
        this.value = value;
    }

}
