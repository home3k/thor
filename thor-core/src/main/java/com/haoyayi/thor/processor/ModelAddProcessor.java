/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import com.google.common.collect.Maps;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.context.meta.FieldContext;
import com.haoyayi.thor.impl.base.OpType;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ModelAddProcessor<T extends BaseType, V extends BaseTypeField> extends AbstractOperationModelProcessor<T, V> implements ModelProcessor<T, V> {

	@SuppressWarnings("unchecked")
	@Override
    public Map<Long, T> process(final String modelType, Map<Long, Map<V, Object>> context) {
        final Map<String, Map<Long, T>> models = getModelModelFactory(modelType).createModel(context);
        final ColumnProcessor columnProcessor = getModelColumnProcessor(modelType);
        // 事务处理
        return (Map<Long, T>) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                try {
                    // 先做subModel
                    Map<String, Map<Long, T>> subProdMod = new HashMap<String, Map<Long, T>>();
                    Map<String, Map<Long, T>> subMultiProdMod = new HashMap<String, Map<Long, T>>();
                    Map<String, Map<Long, T>> mappingSubModels = Maps.newHashMap();
                    for (String model : models.keySet()) {
                        if (!model.equals(modelType)) {
                        	if (columnProcessor.isMultiSubModelField(model)) {
                        		subMultiProdMod.put(model, models.get(model));
                        	} else if (columnProcessor.isMappingSubModelField(model)) {
                        		mappingSubModels.put(model, models.get(model));
                        	} else {
                        		Map<Long, T> subModels = getModelRepository(model).addModel(models.get(model));
                        		subProdMod.put(model, subModels);
                        	}
                        }
                    }
                    // 后做主model
                    Map<Long, T> mainModels = models.get(modelType);
                    if (subProdMod.size() > 0) {
                        mergeSubmodel(modelType, subProdMod, mainModels);
                    }
                    mainModels = getModelRepository(modelType).addModel(mainModels);
                    if (subProdMod.size() > 0) {
                    	setIdForSubModel(subProdMod, mainModels);
                    }
                    if (subMultiProdMod.size() > 0) {
                        mergeMainModel(modelType, subMultiProdMod, mainModels, OpType.ADD);
                    }

                    if (subMultiProdMod.size() > 0) {
                        for (String model : subMultiProdMod.keySet()) {
                            getModelRepository(model).addModel(subMultiProdMod.get(model));
                        }
                    }
                    if (mappingSubModels.size() > 0) {
                    	mergeMainModel(modelType, mappingSubModels, mainModels, OpType.ADD);
                    	mergeSubMappingmodel(modelType, mappingSubModels, mainModels);
                    }
                    if (mappingSubModels.size() > 0) {
                    	for (String subModel : mappingSubModels.keySet()) {
                    		String subModelType = columnProcessor.getSubModelTypeFromSubModelName(subModel);
                            Map<Long, T> subModels = getModelRepository(subModelType).addModel(mappingSubModels.get(subModel));
                            // 获得映射表对象
                            FieldContext subFieldContext = getModelColumnProcessor(modelType).getModelContext().getFieldContext(subModel);
                            String mappingModel = subFieldContext.getRefMappingModel();
                            ColumnProcessor<V> mappingColumnProcessor = getModelColumnProcessor(mappingModel);
                            Map<Long, Map<V, Object>> mappingContexts = Maps.newHashMap();
                            // mainModel对应到mapping映射中的id
                            V mainModelIdField = mappingColumnProcessor.convert(subFieldContext.getRefMappingField2MainModelField().get(mappingModel));
                            // subModel对应到mapping映射中的id
                            V subModelIdField = mappingColumnProcessor.convert(subFieldContext.getRefMappingField2SubModelField().get(mappingModel));
                            Long index = 0l;
                            for (Long subModelId : subModels.keySet()) {
                            	T t = subModels.get(subModelId);
                            	Long mainModelId = t.getRefModelPk();
                            	Map<V, Object> mappingContext = Maps.newHashMap();
                            	mappingContext.put(mainModelIdField, mainModelId);
                            	mappingContext.put(subModelIdField, t.getId());
                            	mappingContexts.put(index++, mappingContext);
                            }
                            process(mappingModel, mappingContexts);
                        }
                    }
                    // 多对多映射情况下，对于子model直接赋值已存在的id的处理方式
                    Map<String, String> subModelId2refMappingFields = columnProcessor.getSubModelIdField2refMappingField();
                    for (T model : mainModels.values()) {
                    	for (String subModelIdField : subModelId2refMappingFields.keySet()) {
                    		Number[] subIds = (Number[]) PropertyUtils.getProperty(model, subModelIdField);
                    		if (ArrayUtils.isNotEmpty(subIds)) {
                    			String subModelName = (String) columnProcessor.getSubModelIdField2subModelField().get(subModelIdField);
                    			FieldContext subFieldContext = getModelColumnProcessor(modelType).getModelContext().getFieldContext(subModelName);
                    			String mappingModel = subModelId2refMappingFields.get(subModelIdField);
                    			ColumnProcessor<V> mappingColumnProcessor = getModelColumnProcessor(mappingModel);
                                Map<Long, Map<V, Object>> mappingContexts = Maps.newHashMap();
                                V mainModelIdFieldV = mappingColumnProcessor.convert(subFieldContext.getRefMappingField2MainModelField().get(mappingModel));
                                V subModelIdFieldV = mappingColumnProcessor.convert(subFieldContext.getRefMappingField2SubModelField().get(mappingModel));
                                Long index = 0l;
                                for (Number subModelId : subIds) {
                                	Map<V, Object> mappingContext = Maps.newHashMap();
                                	mappingContext.put(mainModelIdFieldV, model.getId());
                                	mappingContext.put(subModelIdFieldV, subModelId.longValue());
                                	mappingContexts.put(index++, mappingContext);
                                }
                                process(mappingModel, mappingContexts);
                    		}
                    	}
                    }
                    return mainModels;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }
        });

    }
}