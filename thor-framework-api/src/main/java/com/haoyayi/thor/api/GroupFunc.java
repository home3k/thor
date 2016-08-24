/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * Group by Function
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public enum GroupFunc {

    MAX {
        @Override
        public String getFuncString(Object... param) {
            return "max(" + param[0] + ")";
        }

        @Override
        public String getColumnString(Object... param) {
            return "MAX";
        }
    },
    MIN {
        @Override
        public String getFuncString(Object... param) {
            return "min(" + param[0] + ")";
        }

        @Override
        public String getColumnString(Object... param) {
            return "MIN";
        }
    },
    COUNT {
        @Override
        public String getFuncString(Object... param) {
            return "count(" + param[0] + ")";
        }

        @Override
        public String getColumnString(Object... param) {
            return "COUNT";
        }
    },
    SUM {
        @Override
        public String getFuncString(Object... param) {
            return "sum(" + param[0] + ")";
        }

        @Override
        public String getColumnString(Object... param) {
            return "SUM";
        }
    };

    public String getFuncString(Object... param) {
        return name();
    }

    public String getColumnString(Object... param) {
        return name();
    }

    public String getQueryString(Object... param) {
        return getFuncString(param) + " as " + getColumnString();
    }

}
