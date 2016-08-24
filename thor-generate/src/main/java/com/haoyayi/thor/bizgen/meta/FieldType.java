/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.meta;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class FieldType {

    private String metaType;
    private String rawType;
    private String javaType;
    private String mysqlType;
    private int rawTypeLen;

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    public String getRawType() {
        return rawType;
    }

    public void setRawType(String rawType) {
        this.rawType = rawType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getMysqlType() {
        return mysqlType;
    }

    public void setMysqlType(String mysqlType) {
        this.mysqlType = mysqlType;
    }

    public int getRawTypeLen() {
        return rawTypeLen;
    }

    public void setRawTypeLen(int rawTypeLen) {
        this.rawTypeLen = rawTypeLen;
    }

}
