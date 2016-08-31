/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.haoyayi.thor.ModelAware;
import com.haoyayi.thor.context.meta.FieldContext;
import com.haoyayi.thor.context.meta.ModelContext;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.ConditionFunc;
import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.ErrorCode;
import com.haoyayi.thor.api.GroupFunc;
import com.haoyayi.thor.api.Option;
import com.haoyayi.thor.api.OptionOrderby;
import com.haoyayi.thor.biz.BaseProcessor;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.BizUtils;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.context.InvokeContextDict;
import com.haoyayi.thor.context.InvokeContextHolder;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.processor.ColumnProcessor;
import com.haoyayi.thor.processor.ProcessorContext;
import com.haoyayi.thor.repository.ModelConditionQueryRepository;
import com.haoyayi.thor.validate.ConditionValidator;

/**
 * 查询Facade
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractQueryFacade<T extends BaseType, V extends BaseTypeField, C extends ConditionField> extends BaseProcessor implements QueryFacade<T, V, C>, InitializingBean, ModelAware {

    private static Logger LOG = LoggerFactory.getLogger(AbstractQueryFacade.class);

    @Autowired
    private ProcessorContext processorContext;

    private ColumnProcessor<V> columnProcessor;

    Map<V, Set<V>> field4OtherFields = new HashMap<V, Set<V>>();

    protected Map<Long, T> renderModel(Map<Long, T> models) {
        return models;
    }
    
    protected final Map<Long, T> render(Map<Long, T> models) {
    	renderModel(models);
    	for (T model : models.values()) {
    		String source = (String) InvokeContextHolder.getInstance().getBizContext(InvokeContextDict.SOURCE);
    		if (StringUtils.isNotEmpty(source) && source.toLowerCase().startsWith(ModelConstants.SOURCE_TORO)) {
    			renderNonNullProperties4Map(model);
    		} else {
    			renderNonNullProperties(model);
    		}
    	}
    	return models;
    }
    
    protected Map<Long, Map<V, Object>> renderMapType(Map<Long, Map<V, Object>> models) {
    	return models;
    }
    
 // 过滤map中为null的properties
 	@SuppressWarnings("unchecked")
 	private void renderNonNullProperties4Map(Object obj) {
 		for (Field field : obj.getClass().getDeclaredFields()) {
 			if (Map.class.isAssignableFrom(field.getType())) {
 				field.setAccessible(true);
 				try {
 					Map<Object, Object> map = (Map<Object, Object>) field.get(obj);
 					if (MapUtils.isEmpty(map)) {
 						field.set(obj, null);
 					} else {
 						Iterator<Entry<Object, Object>> iterator = map.entrySet().iterator();
 						while (iterator.hasNext()) {
 							Entry<Object, Object> entry = iterator.next();
 							if (entry.getValue() == null) {
 								iterator.remove();
 							}
 						}
 					}
 				} catch (Exception e) {
 				}
 			}
 			if (Object[].class.isAssignableFrom(field.getType())) {
 				field.setAccessible(true);
 				try {
 					Object[] arrs = (Object[]) field.get(obj);
 					if (ArrayUtils.isNotEmpty(arrs)) {
 						for (Object _obj : arrs) {
 							if (Map.class.isAssignableFrom(_obj.getClass())) {
 								Iterator<Entry<Object, Object>> iterator = ((Map<Object, Object>) _obj).entrySet().iterator();
 								while (iterator.hasNext()) {
 									Entry<Object, Object> entry = iterator.next();
 									if (entry.getValue() == null) {
 										iterator.remove();
 									}
 								}
 							}
 						}
 					}
 				} catch (Exception e) {
 				}
 			}
 		}
     }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void renderNonNullProperties(BaseType type) {
    	for (Field field : type.getClass().getDeclaredFields()) {
    		if (Map.class.isAssignableFrom(field.getType())) {
    			field.setAccessible(true);
    			try {
    				Map<Object, Object> map = (Map<Object, Object>) field.get(type);
    				if (MapUtils.isEmpty(map)) {
    					field.set(type, null);
    				} else {
    					renderNonNullProperties(map);
    				}
    			} catch (Exception e) {
    				
    			}
    		}
    		if (Object[].class.isAssignableFrom(field.getType())) {
    			field.setAccessible(true);
    			try {
    				Object[] arrs = (Object[]) field.get(type);
    				if (ArrayUtils.isEmpty(arrs)) {
    					field.set(type, null);
    				} else {
    					renderNonNullProperties(arrs);
    				}
    			} catch (Exception e) {
    			}
    		}
    	}
    }	
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void renderNonNullProperties(Map<Object, Object> map) {
    	Iterator<Entry<Object, Object>> iterator = map.entrySet().iterator();
    	while (iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();
			Object v = entry.getValue();
			if (v == null) {
				iterator.remove();
			} else if (v instanceof Map) {
				renderNonNullProperties((Map<Object, Object>) v);
			} else if (v instanceof Object[]) {
				renderNonNullProperties((Object[]) v);
			} else if (v instanceof BaseType) {
				renderNonNullProperties((BaseType) v);
			}
		}
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void renderNonNullProperties(Object[] arrs) {
    	for (Object v : arrs) {
    		if (v instanceof Map) {
				renderNonNullProperties((Map<Object, Object>) v);
			} else if (v instanceof Object[]) {
				renderNonNullProperties((Object[]) v);
			} else if (v instanceof BaseType) {
				renderNonNullProperties((BaseType) v);
			}
    	}
    }
    
	
    public Set<V> addOtherFields(Set<V> fields, String model, Set<String> otherModels, Map<String, String> subModelField4model) {
        Set<V> result = new HashSet<V>();

        if (!otherModels.contains(model)) {
            for (String subModel : subModelField4model.keySet()) {
                FieldContext modelField = columnProcessor.getModelContext().getRefModel4Field().get(subModel);
                for (String field : modelField.getModelField4RefModelField().keySet()) {
                    String refModelField = modelField.getModelField4RefModelField().get(field);
                    result.add(columnProcessor.convert(refModelField));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fields)) {
        	for (V field : fields) {
        		result.add(field);
        		result.addAll(field4OtherFields.get(field));
        	}
        }
        result.add(columnProcessor.getPkField());
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        columnProcessor = processorContext.getConverter(getModelType().name());
        ModelContext modelContext = columnProcessor.getModelContext();
        for (V field : columnProcessor.getFields()) {
            Set<V> otherFields = new HashSet<V>();
            FieldContext fieldContext = modelContext.getField4meta().get(field.toString());
            if (fieldContext == null) {
                continue;
            }

            Set<String> strOtherFields = fieldContext.getRelationFields();
            for (String strOtherField : strOtherFields) {
                otherFields.add(columnProcessor.convert(strOtherField));
            }
            field4OtherFields.put(field, otherFields);
        }
    }

    /**
     * @param model2fields
     * @param model
     * @param field
     */
    private void refreshField(Map<String, Set<V>> model2fields, String model, String field) {
        ColumnProcessor<V> columnProcessor = processorContext.getConverter(model);
        Set<V> fields = model2fields.get(model);
        if (fields == null) {
            fields = new HashSet<V>();
        }
        fields.add(columnProcessor.convert(field));
        model2fields.put(model, fields);
    }

    private void refreshFields(Set<String> fields, Set<String> otherModels, Map<String, Set<V>> model2fields, Map<String, String> subModelField4model) {
        for (String field : fields) {
            if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
                List<String> model2field = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
                String model = columnProcessor.getSubModel(model2field.get(0));
                String mField = model2field.get(1);
                subModelField4model.put(model2field.get(0), model);
                refreshField(model2fields, model, mField);
                otherModels.add(model);
            } else {
                refreshField(model2fields, getModelType(), field);
            }
        }
    }

    /**
     * @param model2condtions
     * @param model
     * @param field
     * @param pair
     */
    private void refreshCondition(Map<String, List<ConditionPair<C>>> model2condtions, String model, String field, ConditionPair<C> pair) {
        ColumnProcessor<V> columnProcessor = processorContext.getConverter(model);
        List<ConditionPair<C>> condtion = model2condtions.get(model);
        if (condtion == null) {
            condtion = new ArrayList<ConditionPair<C>>();
        }
        V enumField = columnProcessor.convert(field);
        pair.setField((C) enumField);
        condtion.add(pair);
        model2condtions.put(model, condtion);
    }

    private void refreshConditions(ConditionPair<C>[] conditions, Set<String> otherModels, Map<String, List<ConditionPair<C>>> model2condtions, Map<String, String> subModelField4model) {
        if (conditions == null || conditions.length == 0) {
            return;
        }
        for (ConditionPair<C> condition : conditions) {
            String field = condition.getField().toString();
            if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
                List<String> model2field = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
                String model = columnProcessor.getSubModel(model2field.get(0));
                String mField = model2field.get(1);
                subModelField4model.put(model2field.get(0), model);
                refreshCondition(model2condtions, model, mField, condition);
                otherModels.add(model);
            } else {
                refreshCondition(model2condtions, getModelType(), field, condition);
            }
        }
    }

    private void refreshOptions(String mainModel, Map<String, List<Option>> model2Options, Option[] options) {
        if (options == null) {
            return;
        }
        for (Option option : options) {
            if (option == null) {
                continue;
            }
            if (option instanceof OptionOrderby) {
                OptionOrderby orderby = (OptionOrderby) option;
                Map<String, Boolean> orderbyMap = orderby.getOrderby();
                if(orderbyMap == null) {
                		continue;
                }
                for (String field : orderbyMap.keySet()) {
                    if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
                        List<String> model2field = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
                        String model = columnProcessor.getSubModel(model2field.get(0));
                        String mField = model2field.get(1);
                        OptionOrderby subOrderby = new OptionOrderby();
                        Map<String, Boolean> subOrderbyMap = new HashMap<String, Boolean>();
                        subOrderbyMap.put(mField, orderbyMap.get(field));
                        subOrderby.setOption(subOrderbyMap);
                        refreshModelOptions(model, model2Options, subOrderby);
                    } else {
                        refreshModelOptions(mainModel, model2Options, orderby);
                    }
                }
            } else {
                refreshModelOptions(mainModel, model2Options, option);
            }
        }
    }

    private void refreshModelOptions(String model, Map<String, List<Option>> model2Options, Option option) {
        List<Option> optionList = model2Options.get(model);
        if (optionList == null) {
            optionList = new ArrayList<Option>();
        }
        optionList.add(option);
        model2Options.put(model, optionList);

    }
    
    protected Map<Long, Map<V, Object>> query(Set<V> fields, List<ConditionPair<C>> conditions, Option[] options) {
        ModelConditionQueryRepository<T, V, C> conditionQueryRepository = processorContext.getQueryRepository(columnProcessor.getModelContext().getName());
        return conditionQueryRepository.getModelByCondition(optid, conditions, options, fields);
    }
    
    protected Long queryCnt(String model, List<ConditionPair<C>> conditions) {
    	 ModelConditionQueryRepository<T, V, C> conditionQueryRepository = processorContext.getQueryRepository(getModelType().name());
    	 Map<V, Object> field4Condition = new HashMap<V, Object>();
    	 for (ConditionPair<C> conditionPair : conditions) {
    		 field4Condition.put(columnProcessor.convert(conditionPair.getField().toString()), conditionPair);
    	 }
         Long count = conditionQueryRepository.getModelCountByCondition(optid, field4Condition);
         return count;
    }
    
    protected List<Map<String, Object>> queryGroup(String model, List<ConditionPair<C>> conditions, Set<V> groupFields, Map<GroupFunc, V> groupFuncs) {
    	ModelConditionQueryRepository<T, V, C> conditionQueryRepository = processorContext.getQueryRepository(getModelType().name());
    	Map<V, Object> field4Condition = new HashMap<V, Object>();
	   	for (ConditionPair<C> conditionPair : conditions) {
	   		field4Condition.put(columnProcessor.convert(conditionPair.getField().toString()), conditionPair);
	   	}
    	return conditionQueryRepository.getModelGroupByByCondition(field4Condition, groupFields, groupFuncs);
    }
    
    protected ColumnProcessor<V> getColumnProcessor() {
    	return columnProcessor;
    }
    
    protected Optional<Map<Long, Map<V, Object>>> build(String model, Set<String> otherModels, Set<V> fields, List<ConditionPair<C>> conditions, List<Option> options, Map<Long, T> mainModels, Map<String, String> subModelField4model) throws Exception {

        ConditionValidator<C> validator = processorContext.getConditionValidator(model);
        QueryFacade<T, V, C> queryFacade = processorContext.getQuery(model);
        ModelConditionQueryRepository<T, V, C> conditionQueryRepository = processorContext.getQueryRepository(model);

        if (model.equals(getModelType())) {
            // 验证
            CheckResult<List<ConditionPair<C>>> validateResult = validator.validate(conditions);

            if (validateResult.isErrorResult()) {
                return Optional.fromNullable(null);
            }
        }

        // 填充fields
        Set<V> mainFields = queryFacade.addOtherFields(fields, model, otherModels, subModelField4model);

        if (mainModels != null) {
            // 刷新condition
            conditions = refreshSubConditions(model, conditions, mainModels, subModelField4model);
        }
        Option[] optionArray = null;
        if (options != null) {
            optionArray = new Option[options.size()];
            for (int i = 0; i < options.size(); i++) {
                optionArray[i] = options.get(i);
            }
        }

        // 进行条件查询
        Map<Long, Map<V, Object>> modelFields = conditionQueryRepository.getModelByCondition(optid, conditions, optionArray, mainFields);
        if (MapUtils.isEmpty(modelFields)) {
            return Optional.fromNullable(null);
        }
        return Optional.of(modelFields);

    }

    /**
     * 暂时只支持主条件的 group by查询。
     *
     * @param optid
     * @param conditions
     * @param groupByFields
     * @param groupFuncMap
     * @return
     */
    @Override
    public CheckResult<List<Map<String, Object>>> query(ConditionPair<C>[] conditions, Set<V> groupByFields, Map<GroupFunc, V> groupFuncMap) {
        try {
            // 1. 初始化
            init(optid, getModelType(), OpType.QUERY);
            ModelGroupTree modelGroupTree = new ModelGroupTree().rootOf(getModelType().name(), optid, processorContext);
            modelGroupTree.with((ConditionPair<ConditionField>[]) conditions, (Set<BaseTypeField>) groupByFields, (Map<GroupFunc,BaseTypeField>) groupFuncMap);
            List<Map<String, Object>> groupByResult = modelGroupTree.queryGroup();
//            Map<V, Object> field4Condition = new HashMap<V, Object>();
//            for (ConditionPair<C> condition : conditions) {
//                if (condition == null) {
//                    return new CheckResult<List<Map<String, Object>>>(BizError.getBizError(ErrorCode.ERROR_PARAM_COMMON_ERROR, "the condition fields invalid", "optid"));
//                }
//                String field = condition.getField().toString();
//                if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
//                    LOG.error("Query count not support submodel conditions.");
//                    BizError error = BizError.getBizError(ErrorCode.ERROR_PARAM_COMMON_ERROR,
//                            "Query count not support submodel conditions.", "optId");
//                    return new CheckResult<List<Map<String, Object>>>(error);
//                } else {
//                    V enumField = columnProcessor.convert(field);
//                    condition.setField((C) enumField);
//                    field4Condition.put(enumField, condition);
//                }
//            }
//            ModelConditionQueryRepository<T, V, C> conditionQueryRepository = processorContext.getQueryRepository(getModelType().name());
//            List<Map<String, Object>> groupByResult = conditionQueryRepository.getModelGroupByByCondition(optid, field4Condition, groupByFields, groupFuncMap);
            return new CheckResult<List<Map<String, Object>>>(groupByResult);
        } catch (Exception e) {
            // 全局异常。 直接返回
            LOG.error("Process with exception:", e);
            BizError error = BizError.getBizError(ErrorCode.ERROR_PARAM_COMMON_ERROR,
                    "Process model error! with exception :" + e.getMessage(), "optId");
            return new CheckResult<List<Map<String, Object>>>(error);
        } finally {
            clean();
        }
    }

    /**
     * 暂时只支持主条件count查询。
     *
     * @param optid
     * @param conditions
     * @return
     */
    @Override
    public CheckResult<Long> query(ConditionPair<C>[] conditions) {
        try {
            // 1. 初始化
            init(optid, getModelType(), OpType.QUERY);
            ModelCntTree modelCntTree = new ModelCntTree().rootOf(getModelType().name(), optid, processorContext);
            modelCntTree.with((ConditionPair<ConditionField>[]) conditions);
            return new CheckResult<Long>(modelCntTree.queryCnt());
        } catch (Exception e) {
            // 全局异常。 直接返回
            LOG.error("Process with exception:", e);
            BizError error = BizError.getBizError(ErrorCode.ERROR_PARAM_COMMON_ERROR,
                    "Process model error! with exception :" + e.getMessage(), "optId");
            return new CheckResult<Long>(error);
        } finally {
            clean();
        }
    }

    @Override
    public Map<Long, CheckResult<T>> query(ConditionPair<C>[] conditions, Option[] options, Set<String> fields) {

        try {

            // 1. 初始化
            init(getModelType(), OpType.QUERY);
            ModelTree modelTree = new ModelTree().rootOf(getModelType().name(), processorContext);
            addOtherFields(fields);
            modelTree.with(fields, (ConditionPair<ConditionField>[]) conditions, options);
            Map<Long, T> queryResult = modelTree.query();
            return BizUtils.returnModels(queryResult);
            

        } catch (Exception e) {
            // 全局异常。 直接返回
            LOG.error("Process with exception:", e);
            BizError error = BizError.getBizError(ErrorCode.ERROR_PARAM_COMMON_ERROR,
                    "Process model error! with exception :" + e.getMessage(), "optId");
            return BizUtils.errorRange(error, new HashSet<Long>(Arrays.asList(optid)));
        } finally {
            clean();
        }
    }
    
    protected void addOtherFields(Set<String> fields) {
    	return;
    }

    /**
     * @param model
     * @param subConditions
     * @param mainModels
     * @return
     * @throws Exception
     */
    protected List<ConditionPair<C>> refreshSubConditions(String model, List<ConditionPair<C>> subConditions, Map<Long, T> mainModels, Map<String, String> subModelField4model) throws Exception {

        if (mainModels == null || mainModels.size() == 0) {
            return subConditions;
        }

        if (subConditions == null) {
            subConditions = new ArrayList<ConditionPair<C>>();
        }

        Set<Object> subConditionPairSet = new HashSet<Object>();

        for (String field : subModelField4model.keySet()) {
            String subModel = subModelField4model.get(field);
            if (!subModel.equals(model)) {
                continue;
            }
            FieldContext modelField = columnProcessor.getModelContext().getRefModel4Field().get(field);
            for (String subField : modelField.getRefModelField4ModelField().keySet()) {
                String refModelField = modelField.getRefModelField4ModelField().get(subField);
                for (T type : mainModels.values()) {
                    subConditionPairSet.add(PropertyUtils.getProperty(type, subField));
                }
                subConditions.add(new ConditionPair<C>(ConditionFunc.IN, (C) processorContext.getConverter(model).convert(refModelField), subConditionPairSet));
            }
        }

        return subConditions;
    }

    /**
     * @param model
     * @param mainModels
     * @param subModelFields
     * @param mainModelRetain 是否保留主model
     * @return
     */
    protected Map<Long, T> refreshModel(String model, Map<Long, T> mainModels, Map<Long, Map<V, Object>> subModelFields, Map<String, String> subModelField4model, Boolean mainModelRetain) throws Exception {
        Map<Long, T> result = new LinkedHashMap<Long, T>();
        for (String field : subModelField4model.keySet()) {
            String subModel = subModelField4model.get(field);
            if (!subModel.equals(model)) {
                continue;
            }
            FieldContext modelField = columnProcessor.getModelContext().getRefModel4Field().get(field);
            for (String subField : modelField.getRefModelField4ModelField().keySet()) {
                for (Long id : mainModels.keySet()) {
                    T type = mainModels.get(id);
                    Object subFieldValue = PropertyUtils.getProperty(type, subField);
                    if (subModelFields.containsKey(subFieldValue)) {
                    	PropertyUtils.setProperty(type, field, subModelFields.get(subFieldValue));
                    	result.put(id, type);
                    }
                    if (mainModelRetain) {
                    	result.put(id, type);
                    }
                    
                }
            }
        }

        return result;
    }

}
