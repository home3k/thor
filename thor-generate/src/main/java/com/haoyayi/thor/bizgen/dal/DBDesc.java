/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.dal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k
 */
public class DBDesc {

    Map<String, String> typemap = new HashMap<String, String>();

    Set<String> tables = new HashSet<String>();

    public Map<String, String> getTypemap() {
        return typemap;
    }

    public void setTypemap(Map<String, String> typemap) {
        this.typemap = typemap;
    }

    public Set<String> getTables() {
        return tables;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }


}
