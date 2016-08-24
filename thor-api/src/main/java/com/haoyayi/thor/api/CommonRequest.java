/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.api;

import java.util.Map;

/**
 * 基本请求信息。方便特殊信息传输
 *
 * @author home3k
 */
public abstract class CommonRequest {

    /**
     * 特殊信息
     */
    protected Map<RequestExtraDict, Object> extra;

    public Map<RequestExtraDict, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<RequestExtraDict, Object> extra) {
        this.extra = extra;
    }

}
