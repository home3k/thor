/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.factory;

import com.google.common.collect.Maps;
import com.haoyayi.thor.ModelAware;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.bizgen.manager.ContextManager;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import com.haoyayi.thor.context.BizContextDict;
import com.haoyayi.thor.context.BizContextHolder;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.model.ModelPair;
import com.haoyayi.thor.processor.ColumnProcessor;
import com.haoyayi.thor.processor.ProcessorContext;
import com.haoyayi.thor.repository.ModelRepository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Kai Sun (sunkai@baidu.com)
 * @version 1.0
 * @title
 * @description ModelFactory抽象类
 */
public abstract class AbstractModelFactory<T extends BaseType, V extends BaseTypeField> implements ModelFactory<T, V>, InitializingBean, ModelAware {

    @Autowired
    private ContextManager contextManager;

    @Autowired
    private ProcessorContext processorContext;

    private ColumnProcessor<V> columnProcessor;
    

    Map<V, Set<V>> field4OtherFields = new HashMap<V, Set<V>>();

    public Set<V> addRelatedFields(Set<V> fields) {
        Set<V> result = new HashSet<V>();
        if (fields == null) {
            return result;
        }
        
        for (V field : fields) {
            result.add(field);
            Set<V> otherFields = field4OtherFields.get(field);
            if (otherFields!=null) {
                result.addAll(otherFields);
            }
        }
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
     * @return
     */
    protected abstract ModelRepository<T, V> getModelRepository();

    /**
     * 获得old models
     *
     * @return
     */
    public Map<Long, T> buildOldModel(Map<Long, Map<V, Object>> context) {
        Set<V> fields = new HashSet<V>();
        // 填充所有的相关fields
        for (Map<V, Object> values : context.values()) {
            fields.addAll(values.keySet());
        }
        // 根据response进行渲染
        fields = renderResponse(fields);
        fields.add(columnProcessor.getPkField());
        // 增加ref-fields相关的fields
        // 调用repository获得old models
        if (fields.size() > 0)
            return getModelRepository().getModelById(context.keySet(), fields);
        else
            return Collections.emptyMap();
    }

    /**
     * 根据Request中的field字段，增加对fields的渲染
     *
     * @param fields
     * @return
     */
    protected Set<V> renderResponse(Set<V> fields) {
        // 判断是否设置了res_fields. 如果设置了，需要将其进行填充
        Object resFieldsObj = BizContextHolder.getInstance().getBizContext(BizContextDict.RES_FIELDS);
        fields.addAll(getListenerFields());
        fields = addRelatedFields(fields);
        if (resFieldsObj == null) {
            return fields;
        }
        Map<Long, V[]> refFields = (Map<Long, V[]>) resFieldsObj;
        for (V[] values : refFields.values()) {
            if (values == null) {
                continue;
            }
            for (V value : values) {
                fields.add(value);
            }
        }

        return fields;
    }

    /**
     * @param optid
     * @param context
     * @return
     */
    @Override
    public Map<String, Map<Long, T>> createModel(Long optid, Map<Long, Map<V, Object>> context) {
        Map<String, Map<Long, T>> model2result = new LinkedHashMap<String, Map<Long, T>>();
        Map<Long, T> result = new LinkedHashMap<Long, T>();
        // 生成物料id
        Map<Long, Long> modelIds = generateMaterialIdMap(context);

        // 这里没有做一些before add之类的预处理动作。
        for (Long index : context.keySet()) {
            Map<V, Object> contextField = context.get(index);
            T newModel = convertAddModel(optid, contextField, modelIds.get(index));
            newModel.setId(modelIds.get(index));
            result.put(index, newModel);
        }
        model2result.put(getModelType().name(), result);
        // 如果没有 submodel 直接返回。
        if (!columnProcessor.containsSubModel() && !columnProcessor.containsRefMappingModel()) {
            return model2result;
        }
        // 处理拆分后的model，对于add操作，只能add submodel
        Map<String, Map<OpType, Map<Long, Map<V, Object>>>> subModels = columnProcessor.getSubModels(context, OpType.ADD);
        for (String model : subModels.keySet()) {
            Map<OpType, Map<Long, Map<V, Object>>> subModelContext = subModels.get(model);
            Map<String, Map<Long, T>> prodSubModels = processorContext.getFactory(model).createModel(optid, subModelContext.get(OpType.ADD));
            model2result.putAll(prodSubModels);
        }
        Map<String, Map<OpType, Map<Long, Map<V, Object>>>> refMappingModels = columnProcessor.getRefMappingModels(context, OpType.ADD);
        for (String model : refMappingModels.keySet()) {
            Map<OpType, Map<Long, Map<V, Object>>> refMappingModelContext = refMappingModels.get(model);
            if (MapUtils.isNotEmpty(refMappingModelContext.get(OpType.ADD))) {
            	Map<String, Map<Long, T>> prodSubModels = processorContext.getFactory(columnProcessor.getSubModelTypeFromSubModelName(model)).createModel(optid, refMappingModelContext.get(OpType.ADD));
            	Map<String, Map<Long, T>> models = Maps.newHashMap();
            	for (String key : prodSubModels.keySet()) {
            		models.put(columnProcessor.getSubModelNameFromSubModelType(key), prodSubModels.get(key));
            	}
            	model2result.putAll(models);
            }
        }
        return model2result;
    }


    /**
     * @param optid
     * @param context
     * @return
     */
    @Override
    public Map<String, Map<Long, ModelPair<T, V>>> modModel(Long optid, Map<Long, Map<V, Object>> context) {
        Map<String, Map<Long, ModelPair<T, V>>> model2result = new LinkedHashMap<String, Map<Long, ModelPair<T, V>>>();
        // 处理拆分后的model，对于mod操作，可能mod，可能修改。
        if (columnProcessor.containsSubModel()) {
            Map<String, Map<OpType, Map<Long, Map<V, Object>>>> subModels = columnProcessor.getSubModels(context, OpType.MOD);
            for (String model : subModels.keySet()) {
                Map<OpType, Map<Long, Map<V, Object>>> subModelContext = subModels.get(model);
                Map<String, Map<Long, ModelPair<T, V>>> modSubModels = processorContext.getFactory(model).modModel(optid, subModelContext.get(OpType.MOD));
                model2result.putAll(modSubModels);
                Map<String, Map<Long, T>> addSubModels = processorContext.getFactory(model).createModel(optid, subModelContext.get(OpType.ADD));
                model2result.putAll(convert(addSubModels));
            }
        }
        Map<String, Map<OpType, Map<Long, Map<V, Object>>>> refMappingModels = columnProcessor.getRefMappingModels(context, OpType.MOD);
        for (String model : refMappingModels.keySet()) {
            Map<OpType, Map<Long, Map<V, Object>>> refMappingModelContext = refMappingModels.get(model);
            if (refMappingModelContext.containsKey(OpType.MOD)) {
            	Map<String, Map<Long, ModelPair<T, V>>> prodSubModels4mod = processorContext.getFactory(columnProcessor.getSubModelTypeFromSubModelName(model)).modModel(optid, refMappingModelContext.get(OpType.MOD));
            	Map<String, Map<Long, ModelPair<T, V>>> models = Maps.newHashMap();
            	for (String key : prodSubModels4mod.keySet()) {
            		models.put(columnProcessor.getSubModelNameFromSubModelType(key), prodSubModels4mod.get(key));
            	}
            	model2result.putAll(models);
            }
            if (refMappingModelContext.containsKey(OpType.ADD)) {
            	Map<String, Map<Long, T>> prodSubModels4add = processorContext.getFactory(columnProcessor.getSubModelTypeFromSubModelName(model)).createModel(optid, refMappingModelContext.get(OpType.ADD));
            	Map<String, Map<Long, T>> models = Maps.newHashMap();
            	for (String key : prodSubModels4add.keySet()) {
            		models.put(columnProcessor.getSubModelNameFromSubModelType(key), prodSubModels4add.get(key));
            	}
            	model2result.putAll(convert(models));
            } 
        }
        Map<Long, T> oldModels = buildOldModel(context);
        refreshOldModels(oldModels, model2result);
        model2result.put(getModelType(), createModel4Mod(optid, oldModels, context));
        return model2result;
    }

    protected void refreshOldModels(Map<Long, T> oldModels, Map<String, Map<Long, ModelPair<T, V>>> subModels) {
        Map<Long, List<T>> id2Container = new HashMap<Long, List<T>>();
        for (String subModel : subModels.keySet()) {
            for (ModelPair<T, V> pair : subModels.get(subModel).values()) {
                T subOldModel = pair.getOldModel();
                if (subOldModel != null) {
                	Long id = subOldModel.getRefModelPk();
                	List<T> containers = id2Container.get(id);
                	if (containers == null)
                		containers = new ArrayList<T>();
                	containers.add(subOldModel);
                	id2Container.put(id, containers);
                }
            }
        }
        for (Long id : id2Container.keySet()) {
            T oldModel = oldModels.get(id);
            if (oldModel != null) {
                oldModel.setContainer(id2Container.get(id));
            }
        }
    }

    protected Map<String, Map<Long, ModelPair<T, V>>> convert(Map<String, Map<Long, T>> prodSubModels) {
        Map<String, Map<Long, ModelPair<T, V>>> result = new LinkedHashMap<String, Map<Long, ModelPair<T, V>>>();
        for (String model : prodSubModels.keySet()) {
            Map<Long, ModelPair<T, V>> pairs = new HashMap<Long, ModelPair<T, V>>();
            for (Long id : prodSubModels.get(model).keySet()) {
                pairs.put(id, new ModelPair<T, V>(null, prodSubModels.get(model).get(id)));
            }
            if (pairs.size() > 0) {
                result.put(model, pairs);
            }
        }
        return result;
    }

    @Override
    public Map<Long, T> delModel(Long optid, Map<Long, Map<V, Object>> context) {
        Set<V> fields = new HashSet<V>();
        // 根据response进行渲染
        fields = renderResponse(fields);
        fields.add(columnProcessor.getPkField());
        // 如果删除时需要一些field，调用repository获得old models
        if (fields.size() > 0) {
            return getModelRepository().getModelById(context.keySet(), fields);
        } else {
            // 不用进行查询,
            Map<Long, T> result = new HashMap<Long, T>();
            for (Long id : context.keySet()) {
                result.put(id, null);
            }
            return result;
        }
    }


    /**
     * 构建Mod操作的models
     *
     * @param optid
     * @param oldModels
     * @param contexts
     * @return
     */
    protected Map<Long, ModelPair<T, V>> createModel4Mod(Long optid, Map<Long, T> oldModels, Map<Long, Map<V, Object>> contexts) {

        Map<Long, ModelPair<T, V>> result = new HashMap<Long, ModelPair<T, V>>();
        // 这里没有做一些before mod之类的预处理动作。
        // 查询获得原始数据
        for (Long id : contexts.keySet()) {
            T oldModel = oldModels.get(id);
            Map<V, Object> context = contexts.get(id);
            // 获得modmodel
            T newModel = convertModModel(optid, oldModel, context);
            newModel.setId(id);
            oldModel.setId(id);
            oldModel.setRefModelPk(newModel.getRefModelPk());
            result.put(id, new ModelPair<T, V>(oldModel, newModel));
        }
        return result;
    }

    /**
     * 基于old model及context信息，build出new model
     *
     * @param optid
     * @param oldModel
     * @param context
     * @return
     */
    protected abstract T convertModModel(Long optid, T oldModel, Map<V, Object> context);

    /**
     * 基于context信息，及id，build出new model
     *
     * @param context
     * @param id
     * @return
     */
    protected abstract T convertAddModel(Long optid, Map<V, Object> context, Long id);


    /**
     * 批量生成 model IDs, ADD时使用。
     *
     * @param context
     * @return
     */
    protected Map<Long, Long> generateMaterialIdMap(Map<Long, Map<V, Object>> context) {
        Set<Long> keys = context.keySet();
        Map<Long, Long> result = new HashMap<Long, Long>();
        Iterator<Long> keyIterator = keys.iterator();
        if (columnProcessor.getModelContext().getPkField().hasOption() && columnProcessor.getModelContext().getPkField().getFieldOption().getAutoKey()) {
            for (Long key : keys) {
                Long index = keyIterator.next();
                result.put(index, index);
                index++;
            }
            return result;
        } else {
            int toGenSize = 0;
            for (Long key : keys) {
                Map<V, Object> fields = context.get(key);
                Object pk = fields.get(columnProcessor.getPkField());
                Long pkLong = ((Number)pk).longValue();
                if (pk!= null) {
                   result.put(key, pkLong);
                } else {
                   toGenSize++;
                }
            }
            List<Long> ids = genKeyids(toGenSize);
            for (Long id : ids) {
                Long index = keyIterator.next();
                result.put(index, id);
                index++;
            }
        }
        return result;
    }

    /**
     * 生成Key IDs，ADD时使用。
     *
     * @param size
     * @return List<Long>
     * @author sunkai
     */
    protected abstract List<Long> genKeyids(int size);

    protected List<V> getListenerFields() {
        return new ArrayList<V>();
    }

}
