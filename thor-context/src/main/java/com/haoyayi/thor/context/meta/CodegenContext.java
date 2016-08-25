/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.context.meta;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CodegenContext {

    private Map<String,String> typemap;

    private Map<String,String> typedb;

    private Map<String,Integer> typedblen;

    public Map<String, Integer> getTypedblen() {
        return typedblen;
    }

    public void setTypedblen(Map<String, Integer> typedblen) {
        this.typedblen = typedblen;
    }

    public Map<String, String> getTypedb() {
        return typedb;
    }

    public void setTypedb(Map<String, String> typedb) {
        this.typedb = typedb;
    }

    public Map<String, String> getTypemap() {
        return typemap;
    }

    public void setTypemap(Map<String, String> typemap) {
        this.typemap = typemap;
    }
}
