/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 条件函数
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public enum ConditionFunc {

    LIKE {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " like \"%" + param[0] + "%\"";
            }
            return field + " BETWEEN " + param[0] + " AND " + param[1];
        }
    },

    /**
     * BETWEEN AND
     */
    BETWEEN {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " BETWEEN \"" + param[0] + "\" AND \"" + param[1] + "\"";
            }
            return field + " BETWEEN " + param[0] + " AND " + param[1];
        }
    },

    /**
     * =
     */
    EQ {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " = \"" + param[0] + "\"";
            }
            return field + " = " + param[0];
        }
    },

    /**
     * =
     */
    ISNULL {
        @Override
        public String getPairString(Object field, Object... param) {
            return field + " IS NULL";
        }
    },

    ISNOTNULL {
        @Override
        public String getPairString(Object field, Object... param) {
            return field + " IS NOT NULL";
        }
    },

    /**
     * >
     */
    GT {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " > \"" + param[0] + "\"";
            }
            return field + " > " + param[0];
        }
    },

    /**
     * >=
     */
    GE {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " >= \"" + param[0] + "\"";
            }
            return field + " >= " + param[0];
        }
    },

    /**
     * <
     */
    LT {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " < \"" + param[0] + "\"";
            }
            return field + " < " + param[0];
        }
    },

    /**
     * <=
     */
    LE {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " <= \"" + param[0] + "\"";
            }
            return field + " <= " + param[0];
        }
    },

    /**
     * <>
     */
    NE {
        @Override
        public String getPairString(Object field, Object... param) {
            if (param[0] instanceof String) {
                return field + " <> \"" + param[0] + "\"";
            }
            return field + " <> " + param[0];
        }
    },

    /**
     * if()
     */
    IF {
        @Override
        public String getPairString(Object field, Object... param) {
            return field + " = IF(" + param[0] + "," + param[1] + "," + param[2] + ")";
        }
    },

    /**
     * &
     */
    AND {
        @Override
        public String getPairString(Object field, Object... param) {
            return field + "&" + param[0] + "=" + param[1];
        }
    },

    /**
     * & !=
     */
    ANDNE {
        @Override
        public String getPairString(Object field, Object... param) {
            return field + "&" + param[0] + " != " + param[1];
        }
    },

    /**
     * in
     */
    IN {
        public String getPairString(Object field, Object... param) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(field);
            stringBuilder.append(" IN (");
            List<String> instr = new ArrayList<String>();
            for (Object object : param) {
                if (object != null) {
                    if (object instanceof Collection) {
                        instr = new ArrayList<String>((Collection) object);
                    } else if (object instanceof String) {
                        instr.add("'" + object.toString() + "'");
                    } else {
                        instr.add(object.toString());
                    }
                }
            }
            stringBuilder.append(StringUtils.join(instr, ","));
            stringBuilder.append(" )");
            return stringBuilder.toString();
        }
    },

    /**
     * 作为子model查询的IN条件，但查询结果不过滤父model
     */
    SUBIN {
        public String getPairString(Object field, Object... param) {
            return IN.getPairString(field, param);
        }
    };

    public String getPairString(Object field, Object... param) {
        return name();
    }
}
