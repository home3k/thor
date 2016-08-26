/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.biz;

import com.haoyayi.thor.ModelAware;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ErrorCode;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.BizUtils;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.conf.BizContext;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.processor.ModelAddProcessor;
import com.haoyayi.thor.processor.ModelDelProcessor;
import com.haoyayi.thor.processor.ModelModProcessor;
import com.haoyayi.thor.processor.ModelProcessor;
import com.haoyayi.thor.utils.ErrorUtils;
import com.haoyayi.thor.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractBizCommandProccessor<T extends BaseType, V extends BaseTypeField> extends BaseProcessor implements InitializingBean, ModelAware {

    protected abstract Validator<V> getValidator();

    @Autowired
    private ModelAddProcessor<T, V> addModelProcessor;

    @Autowired
    private ModelModProcessor<T, V> modModelProcessor;

    @Autowired
    private ModelDelProcessor<T, V> delModelProcessor;

    /**
     * 默认就是用当前action进行处理。 Biz方法可以对其进行overrding. 处理特殊业务.
     *
     * @param okModels
     * @param action
     * @return
     */
    protected Map<OpType, Map<Long, Map<V, Object>>> splitContext(Map<Long, Map<V, Object>> okModels, OpType action) {
        Map<OpType, Map<Long, Map<V, Object>>> result = new LinkedHashMap<OpType, Map<Long, Map<V, Object>>>();
        result.put(action, okModels);
        return result;
    }

    /**
     * 必要的后续渲染
     *
     * @param models
     * @return
     */
    protected Map<Long, T> renderModel(Map<Long, T> models) {
        return models;
    }

    protected ModelProcessor<T, V> getProcessor(OpType action) {
        switch (action) {
            case ADD:
                return addModelProcessor;
            case MOD:
                return modModelProcessor;
            case DEL:
                return delModelProcessor;
            default:
                throw new IllegalStateException("");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private static Logger LOG = LoggerFactory.getLogger(AbstractBizCommandProccessor.class);

    protected Map<Long, CheckResult<T>> build(Long optid, Map<Long, Map<V, Object>> context, OpType action) {
        try {
            // 1. 初始化
            init(optid, getModelType(), action);

            // 2. 验证
            Map<Long, CheckResult<Map<V, Object>>> validateResult =
                    getValidator().validate(optid, context, action);

            // 保存错误信息，获得待处理数据.
            Map<Long, Map<V, Object>> okModels = ErrorUtils.saveCheckResultErrors(validateResult);

            if (okModels == null || okModels.size() == 0) {
                return BizUtils.returnModels(optid, null);
            }

            Map<OpType, Map<Long, Map<V, Object>>> splitContext = splitContext(okModels, action);

            Map<Long, T> afterProcess = new LinkedHashMap<Long, T>();

            for (OpType splitAction : splitContext.keySet()) {

                Map<Long, Map<V, Object>> splitModels = splitContext.get(splitAction);
                // 3. model分片
                List<Map<Long, Map<V, Object>>> shardingModels = BizUtils.sharding(splitModels, BizContext.BIZ_SHARDING_THRESHOLD);

                // 4. 分批处理请求
                for (Map<Long, Map<V, Object>> shardingModel : shardingModels) {
                    try {
                        afterProcess.putAll(getProcessor(splitAction).process(optid, getModelType(), shardingModel));
                    } catch (Exception e) {
                        // 分片处理中的异常。
                        LOG.error("Biz Command Proccessor error:", e);
                        BizError error = BizError.getBizError(ErrorCode.ERROR_SYSTEM_COMMON_ERROR,
                                "Biz Command Proccessor process model error.with exception : " + e.getMessage(),
                                "");
                        BizUtils.saveErrors(error, shardingModel.keySet());
                    }
                }
            }

            // 5. render
            afterProcess = renderModel(afterProcess);

            // 6. 模型合并返回。
            return BizUtils.returnModels(optid, afterProcess);

        } catch (Exception e) {
            // 全局异常。 直接返回
            LOG.error("Process with exception:", e);
            BizError error = BizError.getBizError(ErrorCode.ERROR_SYSTEM_COMMON_ERROR,
                    "Process model error! with exception :" + e.getMessage(), "");
            return BizUtils.errorRange(error, context.keySet());
        } finally {
            clean();
        }
    }

}



