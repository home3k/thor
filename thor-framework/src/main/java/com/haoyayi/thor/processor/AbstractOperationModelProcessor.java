/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.bizgen.manager.ContextManager;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import com.haoyayi.thor.factory.ModelFactory;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.model.ModelPair;
import com.haoyayi.thor.repository.ModelRepository;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractOperationModelProcessor<T extends BaseType, V extends BaseTypeField> implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Autowired
    protected  TransactionTemplate transactionTemplate;
    
    @Autowired
    ProcessorContext processorContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    protected ContextManager contextManager;

    protected Map<String, ModelContext> model4modelContext = new HashMap<String, ModelContext>();

    protected ModelRepository getModelRepository(String model) {
        return applicationContext.getBean(model + "Repository", ModelRepository.class);
    }

    protected ModelFactory getModelModelFactory(String model) {
        return applicationContext.getBean(model + "ModelFactory", ModelFactory.class);
    }

    protected ColumnProcessor getModelColumnProcessor(String model) {
        return applicationContext.getBean(model + "ConvertBiz", ColumnProcessor.class);
    }

    /**
     * submodel merge到主model。
     * 暂时只做 submodel field -> model。
     * @param model
     * @param subProdMod
     * @param mainModels
     */
    protected void mergeSubmodel(String model, Map<String, Map<Long, T>> subProdMod, Map<Long, T> mainModels) {
        ModelContext modelContext = model4modelContext.get(model);
        if (modelContext == null) {
            modelContext = contextManager.getContext(model);
            model4modelContext.put(model, modelContext);
        }
        try {
            for (String subModel : subProdMod.keySet()) {
                Map<Long, T> subMod = subProdMod.get(subModel);
                for (Long subModId : subMod.keySet()) {
                    T subModelContent = subMod.get(subModId);
                    Object value = PropertyUtils.getProperty(subModelContent, "refModelPk");
                    if (value == null || !(value instanceof Long)) {
                        continue;
                    } else {
                        merge2Model(subModelContent, modelContext, mainModels, subModel, (Long) value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    protected void setIdForSubModel(Map<String, Map<Long, T>> subProdMod, Map<Long, T> mainModels) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
    	try {
    		for (String subModelName : subProdMod.keySet()) {
    			Map<Long, T> subModels = subProdMod.get(subModelName);
    			for (T subModel : subModels.values()) {
    				T mainModel = mainModels.get(subModel.getRefModelPk());
    				Map<Object, Object> subMap = (Map<Object, Object>) PropertyUtils.getProperty(mainModel, subModelName);
    				BaseTypeField pk = getModelColumnProcessor(subModelName).getPkField();
    				if (!subMap.containsKey(pk)) {
    					subMap.put(getModelColumnProcessor(subModelName).getPkField(), subModel.getId());
    				}
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    protected void mergeSubMappingmodel(String model, Map<String, Map<Long, T>> subProdMod, Map<Long, T> mainModels) {
        ModelContext modelContext = model4modelContext.get(model);
        if (modelContext == null) {
            modelContext = contextManager.getContext(model);
            model4modelContext.put(model, modelContext);
        }
        try {
            for (String subModel : subProdMod.keySet()) {
            	for (T mainModel : mainModels.values()) {
            		PropertyUtils.setProperty(mainModel, subModel, null);
            	}
                Map<Long, T> subMod = subProdMod.get(subModel);
                for (Long subModId : subMod.keySet()) {
                    T subModelContent = subMod.get(subModId);
                    Object value = PropertyUtils.getProperty(subModelContent, "refModelPk");
                    if (value == null || !(value instanceof Long)) {
                        continue;
                    } else {
                    	mergeSubMappingModel2Model(model, subModelContent, modelContext, mainModels, subModel, (Long) value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 更新subsmodel的主键 一对多
     * 暂时只做 submodel field -> model。
     * @param model
     * @param subProdMod
     * @param mainModels
     */
    protected void updateSubModelPk(String model, Map<String, Map<Long, T>> subProdMod, Map<Long, T> mainModels) {
        ModelContext modelContext = model4modelContext.get(model);
        if (modelContext == null) {
            modelContext = contextManager.getContext(model);
            model4modelContext.put(model, modelContext);
        }
        try {
            for (String subModel : subProdMod.keySet()) {
            		Map<Long, List<T>> mainId2subContents = new HashMap<Long, List<T>>();
                Map<Long, T> subMod = subProdMod.get(subModel);
                for (Long subModId : subMod.keySet()) {
                    T subModelContent = subMod.get(subModId);
                    Object value = PropertyUtils.getProperty(subModelContent, "refModelPk");
                    if (value == null || !(value instanceof Long)) {
                        continue;
                    } 
                    Long mainId = (Long) value;
                    if (mainId2subContents.containsKey(mainId)) {
                    		mainId2subContents.get(mainId).add(subModelContent);
                    } else {
                    		List<T> subs = new ArrayList<T>();
                    		subs.add(subModelContent);
                    		mainId2subContents.put(mainId, subs);
                    }
                		
                }
                for(Long mainId : mainId2subContents.keySet()) {
	                	T mainModel = mainModels.get(mainId);
	            		String field = getModelColumnProcessor(model).getSubModelFieldFromSubModel(subModel);
	            		
	            		Object[] values = (Object[]) PropertyUtils.getProperty(mainModel, field);
	            		FieldContext subFieldContext = modelContext.getRefModel4Field().get(field);
	            		List<T> list = mainId2subContents.get(mainId);
	            		for (int i = 0; i < values.length; i++) {
	            			Object v = PropertyUtils.getProperty(list.get(i), subFieldContext.getRefModelPk());
	            			PropertyUtils.setProperty(values[i], subFieldContext.getRefModelPk(), v);
					}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    
    protected void mergeMainModel(String model, Map<String, Map<Long, T>> subProdMod, Map<Long, T> mainModels, OpType opType) {
        ModelContext modelContext = model4modelContext.get(model);
        if (modelContext == null) {
            modelContext = contextManager.getContext(model);
            model4modelContext.put(model, modelContext);
        }

        Map<Long, Long> model4subMapping = new HashMap<Long, Long>();

        try {

            for (Long id : mainModels.keySet()) {
                T mainModel = mainModels.get(id);
                if(OpType.MOD.equals(opType)) {
                		model4subMapping.put(mainModel.getId(), mainModel.getId());
                } else {
                		model4subMapping.put((Long) mainModel.getContainer(),mainModel.getId());
                }
            }
            for (String subModel : subProdMod.keySet()) {
                Map<Long, T> subMod = subProdMod.get(subModel);
                for (Long subModId : subMod.keySet()) {
                    T subModelContent = subMod.get(subModId);
                    Object value = PropertyUtils.getProperty(subModelContent, "refModelPk");
                    if (value == null || !(value instanceof Long)) {
                        continue;
                    } else {
                        merge2SubModel(model, subModelContent, modelContext, subModel, model4subMapping.get((Long) value));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void merge2Model(T subModelContent, ModelContext modelContext, Map<Long, T> mainModels, String subModel, Long modelId) throws Exception {
        FieldContext modelField = modelContext.getRefModel4Field().get(subModel);
        T mainModel = mainModels.get(modelId);
        if (mainModel == null) {
            return;
        }
        for (String subField : modelField.getRefModelField4ModelField().keySet()) {
            String modelRefField = modelField.getRefModelField4ModelField().get(subField);
            PropertyUtils.setProperty(mainModel, subField, PropertyUtils.getProperty(subModelContent, modelRefField));
        }
    }
    
    protected void mergeSubMappingModel2Model(String model, T subModelContent, ModelContext modelContext, Map<Long, T> mainModels, String subModel, Long modelId) throws Exception {
    	String subModelType = processorContext.getConverter(model).getSubModelTypeFromSubModelName(subModel);
    	T mainModel = null;
        for (T t : mainModels.values()) {
        	if (modelId.equals(t.getId())) {
        		mainModel = t;
        		break;
        	}
        }
        if (mainModel == null) {
            return;
        }
        Map<BaseTypeField, Object> mappingModel = Maps.newHashMap();
        for (Field f : subModelContent.getClass().getDeclaredFields()) {
        	f.setAccessible(true);
        	Object value = f.get(subModelContent);
        	if (value != null) {
        		mappingModel.put(processorContext.getConverter(subModelType).convert(f.getName()), f.get(subModelContent));
        	}
        }
        Map<BaseTypeField, Object>[] subModelValues = (Map<BaseTypeField, Object>[]) PropertyUtils.getProperty(mainModel, subModel);
        List<Map<BaseTypeField, Object>> subModelList = null;
        if (subModelValues == null) {
        	subModelList = Lists.newArrayList();
        	subModelList.add(mappingModel);
        } else {
        	subModelList = Lists.newArrayList(subModelValues);
        	subModelList.add(mappingModel);
        }
        PropertyUtils.setProperty(mainModel, subModel, subModelList.toArray(new Map[0]));
    }
    
    protected void merge2SubModel(String model, T subModelContent, ModelContext modelContext, String subModel, Long modelId) throws Exception {
    		if (processorContext.getConverter(model).isMappingSubModelField(subModel)) {
    			String subModelType = processorContext.getConverter(model).getSubModelTypeFromSubModelName(subModel);
    			PropertyUtils.setProperty(subModelContent, processorContext.getConverter(subModelType).getRefModelPk().toString(), modelId);
    		} else {
    			String field = getModelColumnProcessor(model).getSubModelFieldFromSubModel(subModel);
    			FieldContext modelField = modelContext.getRefModel4Field().get(field);
    			PropertyUtils.setProperty(subModelContent, modelField.getModelField4RefModelField().get(modelContext.getPk()), modelId);
    		}
    }
    
    protected void update2SubModel(String model, T subModelContent, ModelContext modelContext, String subModel, Long modelId) throws Exception {
        String field = getModelColumnProcessor(model).getSubModelFieldFromSubModel(subModel);
//        FieldContext modelField = modelContext.getRefModel4Field().get(field);
        PropertyUtils.setProperty(subModelContent, modelContext.getPk(), modelId);
    }
}
