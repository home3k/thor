/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api.wrapper;

import com.haoyayi.thor.api.HeaderExtraDict;
import com.haoyayi.thor.api.RequestExtraDict;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class GenericRequestWrapper {

    /**
     * 接口版本
     */
    protected String version;

    /**
     * Http header 额外信息，比如授权相关等.
     */
    protected Map<HeaderExtraDict, Object> headExtra;

    /**
     * request中的特殊信息
     */
    protected Map<RequestExtraDict, Object> extra;

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

    public Map<HeaderExtraDict, Object> getHeadExtra() {
        return headExtra;
    }

    public void setHeadExtra(Map<HeaderExtraDict, Object> headExtra) {
        this.headExtra = headExtra;
    }
}
