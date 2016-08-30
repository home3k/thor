/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.validate;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.haoyayi.thor.ModelAware;
import com.haoyayi.thor.api.*;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.exception.BizException;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.processor.ColumnProcessor;
import com.haoyayi.thor.processor.ProcessorContext;
import com.haoyayi.thor.utils.ErrorUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本验证
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractValidator<T extends BaseType, R extends BaseTypeField, C extends ConditionField> implements Validator<R>, ConditionValidator<C>, InitializingBean, ModelAware {

    @Autowired
    private ProcessorContext processorContext;

    private ColumnProcessor<R> columnProcessor;


    @Override
    public void afterPropertiesSet() throws Exception {
        columnProcessor = processorContext.getConverter(getModelType());
    }

    @Override
    public Map<Long, CheckResult<Map<R, Object>>> validate(Map<Long, Map<R, Object>> context, OpType action) {

        switch (action) {
            case MOD:
                context = basicCheck(context, action);
                return validateMod(context);
            case ADD:
                context = basicCheck(context, action);
                return validateAdd(context);
            case DEL:
                context = basicCheck(context, action);
                return validateDel(context);
            default:
                throw new BizException("The operation type is invalid!");
        }
    }

    /**
     * 基本的参数验证，及转化，后续会做表达式验证
     *
     * @param context
     */
    protected Map<Long, Map<R, Object>> basicCheck(Map<Long, Map<R, Object>> context, OpType action) {
        Map<Long, Map<R, Object>> checkResultMap = new LinkedHashMap<Long, Map<R, Object>>();
        for (Long id : context.keySet()) {
            Map<R, Object> con = context.get(id);
            int size = con.size();
            int index = 0;
            for (R r : con.keySet()) {
                index++;
                Object value = con.get(r);

                // 包含子model，需要进行dispatch check
                if (columnProcessor.isSubModelField(r.toString())) {

                    String model = columnProcessor.getSubModel(r.toString());
                    Map<OpType, Map<Long, Map<R, Object>>> subModelContext = new LinkedHashMap<OpType, Map<Long, Map<R, Object>>>();
                    Object subModelValue = columnProcessor.buildSubModels(model, value, subModelContext);

                    BizError error = null;

                    for (OpType subModelAction : subModelContext.keySet()) {
                        Map<Long, Map<R, Object>> spSubModelContext = subModelContext.get(subModelAction);
                        if (spSubModelContext.size() == 0) {
                            continue;
                        }
                        Map<Long, CheckResult<Map<R, Object>>> subModelCheckResult = processorContext.getValidator(model).validate(spSubModelContext, subModelAction);
                        for (CheckResult<Map<R, Object>> scr : subModelCheckResult.values()) {
                            if (scr.isErrorResult()) {
                                error = scr.getError();
                                break;
                            }
                        }
                    }

                    if (error != null) {
                        ErrorUtils.saveError(error, id);
                        break;
                    } else {
                        con.put(r, subModelValue);
                        if (index == size) {
                            checkResultMap.put(id, con);
                        }
                        continue;
                    }
                } else if (columnProcessor.isMappingSubModelField(r.toString())) {
                    String subModel = columnProcessor.getMapptingSubModelName(r.toString());
                    Map<OpType, Map<Long, Map<R, Object>>> subModelContextOpTypes = columnProcessor.buildMappingSubModels(null, r.toString(), subModel, value);
                    BizError error = null;
                    for (OpType subModelAction : subModelContextOpTypes.keySet()) {
                        Map<Long, Map<R, Object>> spSubModelContext = subModelContextOpTypes.get(subModelAction);
                        if (spSubModelContext.size() == 0) {
                            continue;
                        }
                        Map<Long, CheckResult<Map<R, Object>>> subModelCheckResult = processorContext.getValidator(subModel).validate(spSubModelContext, subModelAction);
                        for (CheckResult<Map<R, Object>> scr : subModelCheckResult.values()) {
                            if (scr.isErrorResult()) {
                                error = scr.getError();
                                break;
                            }
                        }
                    }
                    if (error != null) {
                        ErrorUtils.saveError(error, id);
                        break;
                    } else {
                        List<Map<R, Object>> subModelContexts = Lists.newArrayList();
                        for (Map<Long, Map<R, Object>> map : subModelContextOpTypes.values()) {
                            subModelContexts.addAll(map.values());
                        }
                        con.put(r, subModelContexts.toArray(new Map[0]));
                        if (index == size) {
                            checkResultMap.put(id, con);
                        }
                        continue;
                    }
                }

                Optional<BizError> optional = columnProcessor.convertFieldValueType(r, value, con);
                if (optional.isPresent()) {
                    ErrorUtils.saveError(optional.get(), id);
                    break;
                }

                if (index == size) {
                    checkResultMap.put(id, con);
                }
            }
        }
        return checkResultMap;
    }

    /**
     * 查询条件验证
     * 只进行基本参数验证。 后续可以进行扩充
     *
     * @param conditions
     * @return
     */
    @Override
    public CheckResult<List<ConditionPair<C>>> validate(List<ConditionPair<C>> conditions) {
        if (conditions != null) {
            for (ConditionPair<C> condition : conditions) {
                if (condition == null || condition.getValue() == null) {
                    return new CheckResult<List<ConditionPair<C>>>(BizError.getBizError(ErrorCode.ERROR_PARAM_COMMON_ERROR, "the condition fields invalid", "optid"));
                }
            }
        }

        CheckResult<List<ConditionPair<C>>> checkResult = new CheckResult<List<ConditionPair<C>>>(conditions);
        return checkResult;
    }

    /**
     * 修改操作验证
     *
     * @param context
     * @return
     */
    protected abstract Map<Long, CheckResult<Map<R, Object>>> validateMod(Map<Long, Map<R, Object>> context);

    /**
     * 添加操作验证
     *
     * @param context
     * @return
     */
    protected abstract Map<Long, CheckResult<Map<R, Object>>> validateAdd(Map<Long, Map<R, Object>> context);

    /**
     * 删除操作验证
     *
     * @param context
     * @return
     */
    protected abstract Map<Long, CheckResult<Map<R, Object>>> validateDel(Map<Long, Map<R, Object>> context);
}
