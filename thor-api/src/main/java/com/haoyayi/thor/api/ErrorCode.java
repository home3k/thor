/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.api;

/**
 * @author home3k
 */
public class ErrorCode {

    /********** 系统异常 **********/

    /**
     * 1 ~ 99
     * 系统通用错误
     */
    /**
     * 系统错误
     */
    public static final int ERROR_SYSTEM_COMMON_ERROR = 3;

    /**
     * 权限错误
     */
    public static final int ERROR_AUTH_COMMON_ERROR = 4;

    /**
     * 参数错误
     */
    public static final int ERROR_PARAM_COMMON_ERROR = 5;

    /**
     * 批量限制错误
     */
    public static final int ERROR_BATCH_EXCEED_ERROR = 6;

    /**
     * 结果错误
     */
    public static final int ERROR_RESULT_ERROR = 7;

    /**
     * 不存在错误
     */
    public static final int ERROR_EXIST_ERROR = 8;

    /**
     * 操作token错误
     */
    public static final int ERROR_TOKEN_ERROR = 9;


}
