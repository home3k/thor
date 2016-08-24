/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class PhoneUtils {
    /**
     * 验证手机号
     */
    protected static Pattern pattern = Pattern.compile("^[1][3,4,7,5,8][0-9]{9}$");

    /**
     * 手机号验证
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Matcher matcher = null;
        boolean matched = false;
        matcher = pattern.matcher(str);
        matched = matcher.matches();
        return matched;
    }

}
