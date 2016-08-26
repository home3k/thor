/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.model.ModelPair;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ModelModProcessor<T extends BaseType, V extends BaseTypeField> extends AbstractOperationModelProcessor<T, V> implements ModelProcessor<T, V> {

	@Autowired
	ModelAddProcessor<T, V> modelAddProcessor;
	@Autowired
	ModelDelProcessor<T, V> modelDelProcessor;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Map<Long, T> process(final Long optid, final String modelType, Map<Long, Map<V, Object>> context) {
        final Map<String, Map<Long, ModelPair<T, V>>> models = getModelModelFactory(modelType).modModel(optid, context);
        final ColumnProcessor columnProcessor = getModelColumnProcessor(modelType);
        
        // 事务处理
        return (Map<Long, T>) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                try {

                    // mod 先不做任何merge工作。先做submodel
                    Map<String, Map<Long, T>> subProdMod = new HashMap<String, Map<Long, T>>();

                    Map<String, Map<Long, T>> subMultiProdMod = new HashMap<String, Map<Long, T>>();
                    Map<String, Map<Long, T>> mappingSubModels = Maps.newHashMap();
                    
                    for (String model : models.keySet()) {
                        if (!model.equals(modelType)) {
                        	if (columnProcessor.isAnytimeNewField(model)) {
                        		ColumnProcessor subColumnProcessor = getModelColumnProcessor(model);
                        		// 清理子对象
                        		Map<Long, ModelPair<T, V>> mainModels = models.get(modelType);
                        		ModelContext mc = columnProcessor.getModelContext();
                        		for (FieldContext fc : mc.getField()) {
                        			if (model.equals(fc.getRefModel())) {
                        				String refField = fc.getModelField4RefModelField().get(fc.getParentContext().getPk());
                        				Map<BaseTypeField, Object> conditions = new HashMap<BaseTypeField, Object>();
                                		conditions.put(subColumnProcessor.convert(refField), mainModels.keySet());
                                		Set<BaseTypeField> fields = new HashSet<BaseTypeField>();
                                		fields.add(subColumnProcessor.getPkField());
                                		Map<Long, Object> originSubModels = getModelRepository(model).getModelByCondition(optid, conditions, fields);
                                		getModelRepository(model).delModelById(optid, originSubModels);
                                		break;
                        			}
                        		}
                        	}
                        	OpType action = checkOp(models.get(model));
                        	if (columnProcessor.isMultiSubModelField(model)) {
                        		// 是一对多,现在只允许要么全部添加，要么全部修改
                        		if (action == OpType.ADD || columnProcessor.isAnytimeNewField(model)) {
                        			subMultiProdMod.put(model, fetchAddModels(models.get(model)));
	                       		} else {
	                       			getModelRepository(model).saveModel(optid, models.get(model)); 
	                       		}
                        	} else if (columnProcessor.isMappingSubModelField(model)) {
                        		// 多对多映射
                        		if (action == OpType.ADD) {
                        			mappingSubModels.put(model, fetchAddModels(models.get(model)));
                        		}
                        		if (action == OpType.MOD) {
                        			mappingSubModels.put(model, fetchAddModels(models.get(model)));
                        			getModelRepository(columnProcessor.getSubModelTypeFromSubModelName(model)).saveModel(optid, models.get(model)); 
                        		}
                        	} else {
                        		Map<Long, T> subModels = null;
	                       		if (action == OpType.MOD) {
	                       			subModels = getModelRepository(model).saveModel(optid, models.get(model));
	                       		} else {
	                       			subModels = getModelRepository(model).addModel(optid, fetchAddModels(models.get(model)));
	                       		}
	                       		subProdMod.put(model, subModels);
                        	}
                        }
                    }

                    // 后做主model
                    Map<Long, ModelPair<T, V>> mainModels = models.get(modelType);
                    Map<Long, T> newModels = new HashMap<Long, T>();
                    for (Long id : mainModels.keySet()) {
                        newModels.put(id, mainModels.get(id).getNewModel());
                    }

                    if (subProdMod.size() > 0) {
                        mergeSubmodel(modelType, subProdMod, newModels);
                    }

                    for (Long id : mainModels.keySet()) {
                        mainModels.get(id).setNewModel(newModels.get(id));
                    }

                    Map<Long, T> saveResults = getModelRepository(modelType).saveModel(optid, mainModels);
                  
                    if (subProdMod.size() > 0) {
                    	setIdForSubModel(subProdMod, saveResults);
                    }
                    
                    if (subMultiProdMod.size() > 0) {
                        mergeMainModel(modelType, subMultiProdMod, newModels, OpType.MOD);
                        
                        for (String subModel : subMultiProdMod.keySet()) {
                        		Map<Long, T> index2model = getModelRepository(subModel).addModel(optid, subMultiProdMod.get(subModel));
                        		Map<String, Map<Long, T>> subModels = new HashMap<String, Map<Long, T>>();
                        		subModels.put(subModel, index2model);
                        		updateSubModelPk(modelType, subModels, newModels);
                        }
                    }
                    if (mappingSubModels.size() > 0) {
                    	mergeMainModel(modelType, mappingSubModels, newModels, OpType.ADD);
                    }
                    if (mappingSubModels.size() > 0) {
                    	for (String subModel : mappingSubModels.keySet()) {
                    		String subModelType = columnProcessor.getSubModelTypeFromSubModelName(subModel);
                            Map<Long, T> subModels = getModelRepository(subModelType).addModel(optid, mappingSubModels.get(subModel));
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
                            Set<Long> mainModelIds = Sets.newHashSet();
                            for (Long subModelId : subModels.keySet()) {
                            	Map<V, Object> mappingContext = Maps.newHashMap();
                            	T t = subModels.get(subModelId);
                            	Long mainModelId = t.getRefModelPk();
                            	mappingContext.put(mainModelIdField, mainModelId);
                            	mappingContext.put(subModelIdField, t.getId());
                            	mappingContexts.put(index++, mappingContext);
                            	mainModelIds.add(mainModelId);
                            }
                            if (subFieldContext.getFieldOption().getAnytimeNew()) {
                            	// 清空原有映射
                            	Map<V, Object> conditions = new HashMap<V, Object>();
                            	conditions.put(mainModelIdField, mainModelIds);
                            	Map<Long, Object> origin = getModelRepository(mappingModel).getModelByCondition(optid, conditions, Sets.newHashSet(mappingColumnProcessor.getPkField()));
                            	if (MapUtils.isNotEmpty(origin)) {
                            		Map<Long,Map<V,Object>> delContext = Maps.newHashMap();
                            		for (Long key : origin.keySet()) {
                            			delContext.put(key, new HashMap());
                            		}
                            		modelDelProcessor.process(optid, mappingModel, delContext);
//                            		getModelRepository(mappingModel).delModelById(optid, origin);
                            	}
                            }
                            modelAddProcessor.process(optid, mappingModel, mappingContexts);
                        }
                    }
                    // 多对多映射情况下，对于子model直接赋值已存在的id的处理方式
                    Map<String, String> subModelId2refMappingFields = columnProcessor.getSubModelIdField2refMappingField();
                    for (T model : saveResults.values()) {
                    	for (String subModelIdField : subModelId2refMappingFields.keySet()) {
                    		Number[] subIds = (Number[]) PropertyUtils.getProperty(model, subModelIdField);
                    		if (ArrayUtils.isNotEmpty(subIds)) {
                    			String subModelName = (String) columnProcessor.getSubModelIdField2subModelField().get(subModelIdField);
                    			FieldContext subFieldContext = getModelColumnProcessor(modelType).getModelContext().getFieldContext(subModelName);
                    			String mappingModel = subModelId2refMappingFields.get(subModelIdField);
                    			ColumnProcessor<V> mappingColumnProcessor = getModelColumnProcessor(mappingModel);
                    			Map<Long, Map<V, Object>> mappingContexts = Maps.newHashMap();
                    			V mainModelIdFieldV = mappingColumnProcessor.convert(subFieldContext.getRefMappingField2MainModelField().get(mappingModel));
                    			V subModelIdFieldV= mappingColumnProcessor.convert(subFieldContext.getRefMappingField2SubModelField().get(mappingModel));
                    			if (subFieldContext.getFieldOption().getAnytimeNew()) {
                    				// 清空原有映射
                    				Map<V, Object> conditions = new HashMap<V, Object>();
                    				conditions.put(mainModelIdFieldV, model.getId());
                    				Map<Long, Object> origin = getModelRepository(mappingModel).getModelByCondition(optid, conditions, Sets.newHashSet(mappingColumnProcessor.getPkField()));
                    				if (MapUtils.isNotEmpty(origin)) {
                                		Map<Long,Map<V,Object>> delContext = Maps.newHashMap();
                                		for (Long key : origin.keySet()) {
                                			delContext.put(key, new HashMap());
                                		}
                                		modelDelProcessor.process(optid, mappingModel, delContext);
//                                		getModelRepository(mappingModel).delModelById(optid, origin);
                                	}
                    			}
                                Long index = 0l;
                                for (Number subModelId : subIds) {
                                	Map<V, Object> mappingContext = Maps.newHashMap();
                                	mappingContext.put(mainModelIdFieldV, model.getId());
                                	mappingContext.put(subModelIdFieldV, subModelId.longValue());
                                	mappingContexts.put(index++, mappingContext);
                                }
                                modelAddProcessor.process(optid, mappingModel, mappingContexts);
                    		}
                    	}
                    }
                    return saveResults;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    private OpType checkOp(Map<Long, ModelPair<T, V>> modelPairMap) {
        for (ModelPair<T, V> modelPair : modelPairMap.values()) {
            if (modelPair.getOldModel() == null) {
                return OpType.ADD;
            } else {
                return OpType.MOD;
            }
        }
        return OpType.MOD;
    }

    private Map<Long, T> fetchAddModels(Map<Long, ModelPair<T, V>> modelPairMap) {
        Map<Long, T> result = new HashMap<Long, T>();
        for (Long id : modelPairMap.keySet()) {
            result.put(id, modelPairMap.get(id).getNewModel());
        }
        return result;
    }
}
