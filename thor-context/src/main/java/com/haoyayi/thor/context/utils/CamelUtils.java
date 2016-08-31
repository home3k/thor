/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.context.utils;

/**
 * Camel utility.
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CamelUtils {

    static final int STEP = 'A' - 'a';

    public static String lowerFirst(String s) {
        return s.replaceFirst(s.substring(0, 1), s.substring(0, 1).toLowerCase());
    }

    public static String upperFirst(String s) {
        return s.replaceFirst(s.substring(0, 1), s.substring(0, 1).toUpperCase());
    }


    public static void main(String args[]) {
        System.out.println(getCamelCaseStr("pay_type1"));
        System.out.println(getUnderScoreCaseStr("payType1"));
    }

    /**
     * @param field
     * @return
     */
    public static String getUnderScoreCaseStr(String field) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < field.length(); i++) {
            char ch = field.charAt(i);
            if (Character.isLetter(ch) && new String(ch + "").equals(new String(ch + "").toUpperCase())) {
                stringBuilder.append("_");
                stringBuilder.append((char) (ch - STEP));
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Get the camel string
     *
     * @param field
     * @return
     */
    public static String getCamelCaseStr(String field) {

        StringBuilder stringBuilder = new StringBuilder("");
        boolean needUpper = false;
        for (int i = 0; i < field.length(); i++) {
            char ch = field.charAt(i);
            if (needUpper) {
                stringBuilder.append((char) (ch + STEP));
                needUpper = false;
                continue;
            }
            if (ch == '_') {
                needUpper = true;
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }
}
