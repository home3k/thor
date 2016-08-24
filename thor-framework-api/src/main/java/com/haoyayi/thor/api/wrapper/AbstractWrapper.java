/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.RequestExtraDict;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractWrapper {

    /**
     * 操作人id
     */
    protected Long optid;

    /**
     * 请求的token信息
     */
    protected String token;
    
    /**
     * 接口版本
     */
    protected String version;

    /**
     * 特殊信息
     */
    protected Map<RequestExtraDict, Object> extra;

    public Long getOptid() {
        return optid;
    }

    public void setOptid(Long optid) {
        this.optid = optid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<RequestExtraDict, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<RequestExtraDict, Object> extra) {
        this.extra = extra;
    }

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
