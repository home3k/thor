/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.io.Serializable;

/**
 * 通用返回结构
 *
 * @author home3k (sunkai@baidu.com)
 */
public class CommonResponse implements Serializable {

    private static final long serialVersionUID = -1998958597718098220L;

    /**
     * 全部成功
     */
    public static final int API_STATUS_OK = 200;

    /**
     * 部分成功
     */
    public static final int API_STATUS_PARTLY_OK = 300;

    /**
     * 全部失败
     */
    public static final int API_STATUS_FAILED = 400;

    private Integer status;

    /**
     * 通用错误信息
     */
    private com.haoyayi.thor.api.Error error;
    
    /**
     * 当前时间戳
     */
    protected Long time;
    
    public CommonResponse() {
		time = System.currentTimeMillis();
	}

	/**
     * 获得最终状态
     *
     * @return
     */
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获得错误信息
     *
     * @return
     */
    public com.haoyayi.thor.api.Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}
