/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import java.util.List;
import java.util.Map;

import com.haoyayi.thor.context.meta.FieldContext;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.repository.ModelRepository;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ModelDelProcessor<T extends BaseType, V extends BaseTypeField> extends AbstractOperationModelProcessor<T, V> implements ModelProcessor<T, V> {

    @SuppressWarnings("unchecked")
	@Override
    public Map<Long, T> process( final String modelType, Map<Long, Map<V, Object>> context) {
        final Map<Long, T> models = getModelModelFactory(modelType).delModel(context);

        // 事务处理
        return (Map<Long, T>) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                try {

                    getModelRepository(modelType).delModelById(models);
                    // 级联删除多对多关系中的映射表数据
                    casDelRefMappings(modelType, models);
                    return models;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }

			private void casDelRefMappings(String modelType, final Map<Long, T> mainModels) {
				ColumnProcessor<V> columnProcessor = getModelColumnProcessor(modelType);
				List<FieldContext> mappingFields = columnProcessor.getModelContext().getRefMappingModelField();
				for (FieldContext mappingField : mappingFields) {
					// 映射表对象名
					String mappingModel = mappingField.getRefMappingModel();
					ColumnProcessor<V> mappingColumnProcessor = getModelColumnProcessor(mappingModel);
					// mainModel对应到mapping映射中的id
				    V mainModelIdField = mappingColumnProcessor.convert(mappingField.getRefMappingField2MainModelField().get(mappingModel));
				    Map<V, Object> conditions = Maps.newHashMap();
				    conditions.put(mainModelIdField, mainModels.keySet());
				    ModelRepository<T, V> mappingRepository = getModelRepository(mappingModel);
				    Map<Long, T> mappings = mappingRepository.getModelByCondition(conditions, Sets.newHashSet(mainModelIdField));
				    if (MapUtils.isNotEmpty(mappings)) {
				    	mappingRepository.delModelById(mappings);
				    }
				}
			}
        });
    }

}