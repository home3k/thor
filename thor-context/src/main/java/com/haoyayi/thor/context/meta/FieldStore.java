/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.context.meta;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class FieldStore {
    private String db;
    private String table;

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}