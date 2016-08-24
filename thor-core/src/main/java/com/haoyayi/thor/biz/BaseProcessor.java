/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz;

import com.haoyayi.thor.context.BizContextDict;
import com.haoyayi.thor.context.BizContextHolder;
import com.haoyayi.thor.context.InvokeContextHolder;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.api.ModelType;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class BaseProcessor {
    /**
     * 进行必要的初始化
     *
     * @param optid
     */
    protected void init(Long optid, ModelType modelType, OpType action) {

        // 设置基本的信息
        BizContextHolder.getInstance().setBizContext(BizContextDict.OPTID, optid);
        BizContextHolder.getInstance().setBizContext(BizContextDict.OP_MODEL, modelType);
        BizContextHolder.getInstance().setBizContext(BizContextDict.OP_ACTION, action);
    }

    /**
     * 进行必要的清理
     */
    protected void clean() {
        BizContextHolder.getInstance().clear();
    }
}
