/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.repository.dict;

import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.Option;
import com.haoyayi.thor.api.dict.dto.*;
import com.haoyayi.thor.dal.*;
import com.haoyayi.thor.dal.bo.*;
import com.haoyayi.thor.dal.meta.*;
import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.repository.AbstractModelRepository;
import com.haoyayi.thor.utils.EnumUtils;
import com.haoyayi.thor.utils.MergeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * ${desc}数据分发层
 */
@Repository
public class ${Model}Repository extends AbstractModelRepository<${Model}Type, ${Model}TypeField, ${Model}ConditionField> {

    <#list bolist as field>
    @Autowired
    private ${field.BO}DAO ${field.bo}DAO;
    </#list>

    <#list newtablelist as nt>
    @Autowired
    private ${nt.BO}DAO ${nt.bo}DAO;
    </#list>

    @Override
    protected Map<Long, ${Model}Type> addModel2DB(Long optid, List<${Model}Type> ${model}s) {
        Map<Long, ${Model}Type> result = new HashMap<Long, ${Model}Type>();
        if (${model}s == null || ${model}s.size()==0) {
            return result;
        }
        // 主表的平铺数据
        <#list bolist as field>
        List<${field.BO}> ${field.bo}List = new ArrayList<${field.BO}>();
        </#list>

        // 关联表数据
        <#list newtablelist as nt>
        List<${nt.BO}> ${nt.bo}List = new ArrayList<${nt.BO}>();
        </#list>

        for (${Model}Type ${model}Type : ${model}s) {
            <#list bolist as field>
            ${field.BO} ${field.bo} = new ${field.BO}();
            BeanUtils.copyProperties(${model}Type, ${field.bo});
            ${field.bo}List.add(${field.bo});
            </#list>

            <#list newtablelist as nt>
            <#if nt.isArray >
            ${nt.MODEL}Type[] ${nt.model}Types = ${model}Type.get${nt.MODEL}();
            if (${nt.model}Types!= null) {
                for(${nt.MODEL}Type ${nt.model}Type : ${nt.model}Types) {
                    ${nt.BO} ${nt.bo} = new ${nt.BO}();
                    BeanUtils.copyProperties(${nt.model}Type, ${nt.bo});
                    <#if nt.newDate >
                    ${nt.bo}.set${nt.DATE}(new java.util.Date());
                    </#if>
                    ${nt.bo}List.add(${nt.bo});
                }
            }
            <#else>
            ${nt.MODEL}Type ${nt.model}Type = ${model}Type.get${nt.MODEL}();
            if (${nt.model}Type != null ) {
                ${nt.BO} ${nt.bo} = new ${nt.BO};
                BeanUtils.copyProperties(${nt.model}Type, ${nt.bo});
                <#if nt.newDate >
                ${nt.bo}.set${nt.DATE}(new java.util.Date());
                </#if>
                ${nt.bo}List.add(${nt.bo});
            }
            </#if>

            </#list>
            <#if !modelAutokey >
            result.put(${model}Type.getId(), ${model}Type);
            </#if>
        }
        List<Long> ids;
        <#if modelAutokey>
        <#list bolist as field>
        ids=${field.bo}DAO.add${field.BO}AutoKey(${field.bo}List);
        </#list>
        for (int i=0;i<ids.size();i++) {
            ${Model}Type ${model}Type = ${model}s.get(i);
            ${model}Type.setId(ids.get(i));
            result.put((long)i, ${model}Type);
        }
        <#else>
        <#list bolist as field>
        ids=${field.bo}DAO.add${field.BO}(${field.bo}List);
        </#list>
        </#if>

        <#list newtablelist as nt>
        <#if nt.autoKey>
        ${nt.bo}DAO.add${nt.BO}AutoKey(${nt.bo}List);
        <#else>
        ${nt.bo}DAO.add${nt.BO}(${nt.bo}List);
        </#if>
        </#list>

        return result;
    }

    @Override
    protected void saveData2DB(Long opuid, Map<Long, Map<${Model}TypeField, Object>> modelid2saver, Map<Long, ${Model}Type> oldmodels, Map<Long, ${Model}Type> newmodels) {
        if (CollectionUtils.isEmpty(modelid2saver)) {
            return;
        }

        <#list bolist as field>
        Map<Long, Map<${field.BO}Column, Object>> id2${field.bo}Column = new HashMap<Long, Map<${field.BO}Column, Object>>();
        Map<${field.BO}Column, Object> ${field.bo}Conditions = new HashMap<${field.BO}Column, Object>();
        </#list>

        <#list newtablelist as nt>
        List<${nt.BO}> add${nt.bo}List = new ArrayList<${nt.BO}>();
        List<Long> del${nt.bo}List = new ArrayList<Long>();
        </#list>

        for (Long id : modelid2saver.keySet()) {
            ${Model}Type oldmodel = oldmodels.get(id);
            ${Model}Type newmodel = newmodels.get(id);
            Map<${Model}TypeField, Object> ${model}Fields = modelid2saver.get(id);
            if (CollectionUtils.isEmpty(${model}Fields)) {
                continue;
            }
            <#list bolist as field>

            Map<${field.BO}Column, Object> ${field.bo}ColumnObjectMap = EnumUtils
                    .filterAndConvertEnumItemMap(${model}Fields, ${field.BO}Column.class);
            if (!CollectionUtils.isEmpty(${field.bo}ColumnObjectMap)) {
                id2${field.bo}Column.put(id, ${field.bo}ColumnObjectMap);
            }
            </#list>

            <#list newtablelist as nt>

            if(${model}Fields.containsKey(${Model}TypeField.${nt.model})){
                <#if nt.isArray >
                ${nt.MODEL}Type[] old${nt.model}Types = oldmodel.get${nt.MODEL}();
                ${nt.MODEL}Type[] new${nt.model}Types = newmodel.get${nt.MODEL}();
                <#else>
                ${nt.MODEL}Type old${nt.model}Type = oldmodel.get${nt.MODEL}();
                ${nt.MODEL}Type new${nt.model}Type = newmodel.get${nt.MODEL}();
                </#if>
                if(old${nt.model}Types==null){
                <#if nt.isArray >
                    for(${nt.MODEL}Type new${nt.model}Type : new${nt.model}Types) {
                        ${nt.BO} new${nt.bo} = new ${nt.BO}();
                        BeanUtils.copyProperties(new${nt.model}Type,new${nt.bo});
                        <#if nt.newDate>
                        new${nt.bo}.set${nt.DATE}(new java.util.Date());
                        </#if>
                        add${nt.bo}List.add(new${nt.bo});
                    }
                <#else>
                    ${nt.BO} new${nt.bo} = new ${nt.BO}();
                    BeanUtils.copyProperties(new${nt.model}Type,new${nt.bo});
                    <#if nt.updateDate>
                    new${nt.bo}.set${nt.DATE}(new java.util.Date());
                    </#if>
                    add${nt.bo}List.add(new${nt.bo});
                </#if>
                } else if(new${nt.model}Types==null) {
                    del${nt.bo}List.add(id);
                } else {
                    del${nt.bo}List.add(id);
                    <#if nt.isArray >
                    for(${nt.MODEL}Type new${nt.model}Type : new${nt.model}Types) {
                        ${nt.BO} new${nt.bo} = new ${nt.BO}();
                        BeanUtils.copyProperties(new${nt.model}Type,new${nt.bo});
                        <#if nt.updateDate>
                        new${nt.bo}.set${nt.DATE}(new java.util.Date());
                        </#if>
                        add${nt.bo}List.add(new${nt.bo});
                    }
                    <#else>
                    ${nt.BO} new${nt.bo} = new ${nt.BO}();
                    BeanUtils.copyProperties(new${nt.model}Type,new${nt.bo});
                    <#if nt.updateDate>
                    new${nt.bo}.set${nt.DATE}(new java.util.Date());
                    </#if>
                    add${nt.bo}List.add(new${nt.bo});
                    </#if>
                }
            }
            </#list>
         }
        <#list bolist as field>
        ${field.bo}DAO.mod${field.BO}(id2${field.bo}Column);
        </#list>

        <#list newtablelist as nt>
        if (del${nt.bo}List.size()>0) {
            Map<${nt.BO}Column, Object> ${nt.bo}Condition = new HashMap<${nt.BO}Column, Object>();
            ${nt.bo}Condition.put(${nt.BO}Column.${nt.refpk}, del${nt.bo}List);
            ${nt.bo}DAO.del${nt.BO}ByCondition(${nt.bo}Condition);
        }
        <#if nt.autoKey>
        ${nt.bo}DAO.add${nt.BO}AutoKey(add${nt.bo}List);
        <#else>
        ${nt.bo}DAO.add${nt.BO}(add${nt.bo}List);
        </#if>
        </#list>
    }

    @Override
    protected void delModel2DB(Long opuid, Map<Long, ${Model}Type> id2model) {
        // 对主表进行删除.
        <#list bolist as field>
        ${field.bo}DAO.del${field.BO}(id2model.keySet());
        </#list>
        // 对关联表进行删除.
        <#list newtablelist as nt>
        Map<${nt.BO}Column, Object> ${nt.bo}Condition = new HashMap<${nt.BO}Column, Object>();
        ${nt.bo}Condition.put(${nt.BO}Column.${nt.refpk}, id2model.keySet());
        ${nt.bo}DAO.del${nt.BO}ByCondition(${nt.bo}Condition);
        </#list>
    }

    @Override
    protected ModelType getModelType() {
        return ModelType.${model};
    }

    @Override
    protected Map<Long, ${Model}Type> getModelFromDB(Set<Long> ids) {
        Map<Long, ${Model}Type> result = new HashMap<Long, ${Model}Type>();
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }

        for(Long id:ids) {
            result.put(id, new ${Model}Type());
        }

        // 主表查询
        <#list bolist as field>
        Map<Long, ${field.BO}> ${field.bo}Map = ${field.bo}DAO.get${field.BO}(ids);
        result = MergeUtils.mergeObjectItems(result, ${field.bo}Map);
        </#list>

        // 关联表查询
        <#list newtablelist as nt>
        Map<${nt.BO}Column, Object> ${nt.bo}Condition = new HashMap<${nt.BO}Column, Object>();
        ${nt.bo}Condition.put(${nt.BO}Column.${nt.refpk}, result.keySet());
        Map<Long, ${nt.BO}> ${nt.bo}Map = ${nt.bo}DAO.get${nt.BO}(${nt.bo}Condition);
        Map<Long, List<${nt.MODEL}Type>> modelid4${nt.model} = new HashMap<Long, List<${nt.MODEL}Type>>();
        for(${nt.BO} ${nt.bo}: ${nt.bo}Map.values()) {
            Long id = ${nt.bo}.get${nt.REFPK}();
            List<${nt.MODEL}Type> ${nt.model}TypeList = modelid4${nt.model}.get(id);
            if (${nt.model}TypeList == null) {
                ${nt.model}TypeList = new ArrayList<${nt.MODEL}Type>();
            }
            ${nt.MODEL}Type ${nt.model}Type = new ${nt.MODEL}Type();
            BeanUtils.copyProperties(${nt.bo}, ${nt.model}Type);
            ${nt.model}TypeList.add(${nt.model}Type);
            modelid4${nt.model}.put(id, ${nt.model}TypeList);
        }
        for(Long id: modelid4${nt.model}.keySet()) {
            <#if nt.isArray >
            result.get(id).set${nt.MODEL}(modelid4${nt.model}.get(id).toArray(new ${nt.MODEL}Type[]{}));
            <#else>
            result.get(id).set${nt.MODEL}(modelid4${nt.model}.get(id).get(0));
            </#if>
        }

        </#list>

        return result;
    }

    @Override
    protected Map<Long, Map<${Model}TypeField, Object>> getModelFieldFromDB(Set<Long> ids, Set<${Model}TypeField> fields) {
        Map<Long, Map<${Model}TypeField, Object>> result = new HashMap<Long, Map<${Model}TypeField, Object>>();
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(fields)) {
            return result;
        }
        Set<${Model}TypeField> fields4Query = new HashSet<${Model}TypeField>(fields);

        <#list bolist as field>
        Set<${field.BO}Column> ${field.bo}Columns = EnumUtils
                .filterAndConvertEnumItem(fields4Query, ${field.BO}Column.class);

        Map<Long, Map<${field.BO}Column, Object>> ${field.bo} = ${field.bo}DAO
                .get${field.BO}(ids, ${field.bo}Columns);

        result = MergeUtils.mergeEnumItems(result, ${Model}TypeField.class, ${field.bo});
        </#list>

        // 关联表查询
        <#list newtablelist as nt>
        if (fields4Query.contains(${Model}TypeField.${nt.model})) {
            Map<${nt.BO}Column, Object> ${nt.bo}Condition = new HashMap<${nt.BO}Column, Object>();
            ${nt.bo}Condition.put(${nt.BO}Column.${nt.refpk}, result.keySet());
            Map<Long, ${nt.BO}> ${nt.bo}Map = ${nt.bo}DAO.get${nt.BO}(${nt.bo}Condition);
            Map<Long, List<${nt.MODEL}Type>> modelid4${nt.model} = new HashMap<Long, List<${nt.MODEL}Type>>();

            for(${nt.BO} ${nt.bo}: ${nt.bo}Map.values()) {
                Long id = ${nt.bo}.get${nt.REFPK}();
                List<${nt.MODEL}Type> ${nt.model}TypeList = modelid4${nt.model}.get(id);
                if (${nt.model}TypeList == null) {
                    ${nt.model}TypeList = new ArrayList<${nt.MODEL}Type>();
                }
                ${nt.MODEL}Type ${nt.model}Type = new ${nt.MODEL}Type();
                BeanUtils.copyProperties(${nt.bo}, ${nt.model}Type);
                ${nt.model}TypeList.add(${nt.model}Type);
                modelid4${nt.model}.put(id, ${nt.model}TypeList);
            }
            for(Long id: modelid4${nt.model}.keySet()) {
                <#if nt.isArray >
                result.get(id).put(${Model}TypeField.${nt.model}, modelid4${nt.model}.get(id).toArray(new ${nt.MODEL}Type[]{}));
                <#else>
                result.get(id).put(${Model}TypeField.${nt.model}, ${nt.MODEL}Type(modelid4${nt.model}.get(id).get(0)));
                </#if>
            }
        }
        </#list>

        return result;
    }

    public Map<Long, ${Model}Type> fillModel(Map<Long, Map<${Model}TypeField, Object>> modelFields) {
        Map<Long, ${Model}Type> result = new LinkedHashMap<Long, ${Model}Type>();
        for (Long modelid : modelFields.keySet()) {
            ${Model}Type model = new ${Model}Type();
            result.put(modelid, model);
        }
        return MergeUtils.mergeObjectEnumItems(result, modelFields);
    }


    @Override
    public Map<Long, Map<${Model}TypeField, Object>> getModelByCondition(Long optid, List<ConditionPair<${Model}ConditionField>> conditions, Option[] options, Set<${Model}TypeField> fields) {
        // 暂时只支持主表查询.
        OptionItem<${Mainbo}Column> option = convertOption(options, ${Mainbo}Column.class);
        Map<${Mainbo}Column, Object> cons = buildConditions(conditions, ${Mainbo}Column.class);
        Set<${Mainbo}Column> boFields = EnumUtils.getFields(fields, ${Mainbo}Column.class);
        Map<Long, Map<${Mainbo}Column, Object>> ${mainbo}Map = ${mainbo}DAO.get${Mainbo}ByCondition(cons, boFields, option.getOrderBys(), option.getNum(), option.getOffset());
        Map<Long, Map<${Model}TypeField, Object>> result = new HashMap<Long, Map<${Model}TypeField, Object>>();
        return MergeUtils.mergeEnumItems(result, ${Model}TypeField.class, ${mainbo}Map);

    }

    @Override
    public Long getModelCountByCondition(Long optid, Map<${Model}TypeField, Object> conditions) {
        Map<${Mainbo}Column, Object> ${mainbo}Conditions = EnumUtils.filterAndConvertEnumItemMap(conditions, ${Mainbo}Column.class);
        return ${mainbo}DAO.getCountByCondition(${mainbo}Conditions);
    }

    @Override
    protected Map<Long, Map<${Model}TypeField, Object>> getModelFieldFromDB(Map<${Model}TypeField, Object> conditions, Set<${Model}TypeField> fields) {
        Map<Long, Map<${Model}TypeField, Object>> result = new HashMap<Long, Map<${Model}TypeField, Object>>();
        if (CollectionUtils.isEmpty(conditions) || CollectionUtils.isEmpty(fields)) {
            return result;
        }
        Set<${Model}TypeField> fields4Query = new HashSet<${Model}TypeField>(fields);

        <#list bolist as field>
        Set<${field.BO}Column> ${field.bo}Columns = EnumUtils
        .filterAndConvertEnumItem(fields4Query, ${field.BO}Column.class);
        Map<${field.BO}Column, Object> ${field.bo}Conditions = EnumUtils.filterAndConvertEnumItemMap(conditions, ${field.BO}Column.class);

        Map<Long, Map<${field.BO}Column, Object>> ${field.bo} = ${field.bo}DAO
        .get${field.BO}(${field.bo}Conditions, ${field.bo}Columns);

        result = MergeUtils.mergeEnumItems(result, ${Model}TypeField.class, ${field.bo});
        </#list>

        // 关联表查询
        <#list newtablelist as nt>
        Map<${nt.BO}Column, Object> ${nt.bo}Condition = new HashMap<${nt.BO}Column, Object>();
        ${nt.bo}Condition.put(${nt.BO}Column.${nt.refpk}, result.keySet());
        Map<Long, ${nt.BO}> ${nt.bo}Map = ${nt.bo}DAO.get${nt.BO}(${nt.bo}Condition);
        Map<Long, List<${nt.MODEL}Type>> modelid4${nt.model} = new HashMap<Long, List<${nt.MODEL}Type>>();

        for(${nt.BO} ${nt.bo}: ${nt.bo}Map.values()) {
            Long id = ${nt.bo}.get${nt.REFPK}();
            List<${nt.MODEL}Type> ${nt.model}TypeList = modelid4${nt.model}.get(id);
            if (${nt.model}TypeList == null) {
                ${nt.model}TypeList = new ArrayList<${nt.MODEL}Type>();
            }
            ${nt.MODEL}Type ${nt.model}Type = new ${nt.MODEL}Type();
            BeanUtils.copyProperties(${nt.bo}, ${nt.model}Type);
            ${nt.model}TypeList.add(${nt.model}Type);
            modelid4${nt.model}.put(id, ${nt.model}TypeList);
        }

        for(Long id: modelid4${nt.model}.keySet()) {
        <#if nt.isArray >
            result.get(id).put(${Model}TypeField.${nt.model}, modelid4${nt.model}.get(id).toArray(new ${nt.MODEL}Type[]{}));
        <#else>
            result.get(id).put(${Model}TypeField.${nt.model}, ${nt.MODEL}Type(modelid4${nt.model}.get(id).get(0)));
        </#if>
        }
    </#list>

        return result;

    }
}
