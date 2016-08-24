/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.factory.${model};

import com.haoyayi.thor.api.${model}.dto.${Model}Type;
import com.haoyayi.thor.api.${model}.dto.${Model}TypeField;
import com.haoyayi.thor.factory.AbstractModelFactory;
import com.haoyayi.thor.repository.ModelRepository;
import com.haoyayi.thor.repository.${model}.${Model}Repository;
import com.haoyayi.thor.sal.ucenter.UcenterIdSAO;
import com.haoyayi.thor.sal.ucenter.impl.UcenterConst;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.haoyayi.thor.api.ModelType;

import java.util.List;
import java.util.Map;
import com.haoyayi.thor.constants.ModelConstants;

/**
 * ${desc}工厂方法，build业务模型 Map<${Model}TypeField, Object> -> ${Model}Type
 */
@Service
public class ${Model}ModelFactory extends AbstractModelFactory<${Model}Type, ${Model}TypeField> {

    @Autowired
    private ${Model}Repository ${model}Repository;

    @Autowired
    private UcenterIdSAO ucenterIdSAO;

    protected ModelType getModelType() {
        return ModelType.${model};
    }

    @Override
    protected ModelRepository<${Model}Type, ${Model}TypeField> getModelRepository() {
        return ${model}Repository;
    }

    @Override
    protected ${Model}Type convertModModel(Long optid, ${Model}Type oldModel, Map<${Model}TypeField, Object> context) {
        ${Model}Type ${model}Type = new ${Model}Type();
        try {
            PropertyUtils.copyProperties(${model}Type, oldModel);
            for (${Model}TypeField field : context.keySet()) {
                PropertyUtils.setProperty(${model}Type, field.name(), context.get(field));
            }
            <#if updateDate >
            ${model}Type.set${UPDATEDATE}(new java.util.Date());
            </#if>
            // TODO 特殊业务

        } catch (Exception e) {
            throw new IllegalStateException("set property  ${Model}ModelFactory.convertModModel", e);
        }
        return ${model}Type;
    }

    @Override
    protected ${Model}Type convertAddModel(Long optid, Map<${Model}TypeField, Object> context, Long id) {
        ${Model}Type ${model}Type = new ${Model}Type();
        ${model}Type.set${Pk}(id);
        try {
            for (${Model}TypeField field : context.keySet()) {
                PropertyUtils.setProperty(${model}Type, field.name(), context.get(field));
            }
            <#if newDate >
            ${model}Type.set${NEWDATE}(new java.util.Date());
            </#if>
            <#if isDel >
            ${model}Type.set${ISDEL}(ModelConstants.IS_NOT_DEL);
            </#if>
            // TODO 特殊业务

        } catch (Exception e) {
            throw new IllegalStateException("set property  ${Model}ModelFactory.convertModModel", e);
        }
        return ${model}Type;
    }


    @Override
    protected List<Long> genKeyids(int size) {
        <#if autokey>
        return null;
        <#else >
        return ucenterIdSAO.generateIds(UcenterConst.${model}, size);
        </#if>
    }

}
