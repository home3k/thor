/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import com.google.common.base.Optional;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.context.meta.ModelContext;
import com.haoyayi.thor.impl.base.OpType;

import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface ColumnProcessor<R extends BaseTypeField> {

    /**
     * 获得所有的fields
     *
     * @return
     */
    R[] getFields();


    R getPkField();

    /**
     * String -> BaseTypeField
     *
     * @param field
     * @return
     */
    R convert(String field);

    /**
     * 获得当前处理model的Context信息
     *
     * @return
     */
    ModelContext getModelContext();

    /**
     * 判断当前model是否包含子model
     *
     * @return
     */
    Boolean containsSubModel();
    
    Boolean containsRefMappingModel();

    /**
     * 判断当前的field是否是子model field
     *
     * @param field
     * @return
     */
    Boolean isSubModelField(String field);

    /**
     * 根据当前field获得其子model名
     *
     * @param field
     * @return
     */
    String getSubModel(String field);

    /**
     * 根据子model名获得其子model的pk
     *
     * @param subModel
     * @return
     */
    String getSubModelPk(String subModel);
    
    String getSubModelRefId(String subModel);
    
    Set<R> getOtherNecessaryFields(R field);

    R getRefModelPk();

    void buildSubModels(Long mainModelPk, String model, Object subModels, Map<OpType, Map<Long, Map<R, Object>>> context, OpType action);
    
    Map<OpType, Map<Long, Map<R, Object>>> buildMappingSubModels(Long mainModelPk, String subModelField, String subModelName, Object value);

    Object buildSubModels(String model, Object subModels, Map<OpType, Map<Long, Map<R, Object>>> context);

    Map<String, Map<OpType, Map<Long, Map<R, Object>>>> getSubModels(Map<Long, Map<R, Object>> context, OpType action);
    
    Map<String, Map<OpType, Map<Long, Map<R, Object>>>> getRefMappingModels(Map<Long, Map<R, Object>> context, OpType action);

    Optional<BizError> convertFieldValueType(R field, Object value, Map<R, Object> context);

    Boolean isMultiSubModelField(String field);
    
    
    Boolean isAnytimeNewField(String field);
    
    Boolean isAnytimeNewMappingField(String field);
    
    /**
     * 判断field是否为一个包含双向映射关系（多对多）的子model。
     * @param field
     * @return
     */
    Boolean isMappingSubModelField(String field);
    
    String getMapptingSubModelName(String field);

    String getSubModelFieldFromSubModel(String field);
    
    String getSubModelTypeFromSubModelName(String field);
    
    String getSubModelNameFromSubModelType(String field);

    Map<String, String> getSubModelIdField2refMappingField();
    
    Map<String, String> getSubModelIdField2subModelField();
}
