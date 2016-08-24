/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.haoyayi.dbrouter.core.anno.DBOperationDesc;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.GroupFunc;
import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.api.MultiConditionPair;
import com.haoyayi.thor.api.Option;
import com.haoyayi.thor.api.OptionLimit;
import com.haoyayi.thor.api.OptionOrderby;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import com.haoyayi.thor.dal.base.AbstractDictDao;
import com.haoyayi.thor.dal.base.BoColumn;
import com.haoyayi.thor.dal.base.DictStoreType;
import com.haoyayi.thor.event.EventHolder;
import com.haoyayi.thor.event.ModelAddEvent;
import com.haoyayi.thor.event.ModelDelEvent;
import com.haoyayi.thor.event.ModelSaveEvent;
import com.haoyayi.thor.model.ModelPair;
import com.haoyayi.thor.processor.ColumnProcessor;
import com.haoyayi.thor.processor.ProcessorContext;
import com.haoyayi.thor.utils.MergeUtils;

/**
 * @param <T>
 * @param <R>
 * @author home3k
 */
public abstract class AbstractModelRepository<T extends BaseType, R extends BaseTypeField, C extends ConditionField> extends CachedModelRepository<T> implements ModelRepository<T, R>,
        ApplicationContextAware, ModelConditionQueryRepository<T, R, C>, InitializingBean {

    private ApplicationContext applicationContext;

    @Autowired
    private ProcessorContext processorContext;

    private ColumnProcessor<R> columnProcessor;

    private ModelContext modelContext;


    private static Logger LOG = LoggerFactory.getLogger(AbstractModelRepository.class);

    /**
     * 添加model到db
     *
     * @param optid
     * @param material
     * @return
     */
    protected abstract Map<Long, T> addModel2DB(Long optid, List<T> material);

    /**
     * 保存更新到db
     */
    protected abstract void saveData2DB(Long opuid, Map<Long, Map<R, Object>> modelid2saver,
                                        Map<Long, T> oldmodel, Map<Long, T> newmodel);

    /**
     * 进行db删除
     *
     * @param opuid
     * @param id2model
     */
    protected abstract void delModel2DB(Long opuid, Map<Long, T> id2model);

    /**
     * @return
     */
    protected abstract ModelType getModelType();


    protected abstract Map<Long, T> getModelFromDB(Set<Long> ids);

    /**
     * 根据model id 查询 model
     *
     * @param ids
     * @return
     */
    public Map<Long, T> getModelById(Set<Long> ids) {

        Map<Long, T> result = new HashMap<Long, T>();
        Set<Long> ids2Get = new HashSet<Long>(ids);

        // 此处先进行thread local查询
        Map<Long, T> data = getModelFromThreadlocal(ids2Get);
        result.putAll(data);
        ids2Get.removeAll(data.keySet());
        ids2Get.removeAll(getDelModelFromThreadlocal(ids2Get).keySet());
        if (ids2Get.isEmpty()) {
//            LOG.info("Cache matched!");
            return result;
        }

        Map<Long, T> models = getModelFromDB(ids2Get);
        for (Long id : models.keySet()) {
            T model = models.get(id);
            if (model == null || model.getId() == null) {
                continue;
            }
            result.put(id, model);
        }

        saveModel2Cache(result);

        return result;
    }

    public abstract Map<Long, T> fillModel(Map<Long, Map<R, Object>> modelFields);

    public Map<Long, T> getModelById(Set<Long> ids, Set<R> fields) {

        Map<Long, T> result = new HashMap<Long, T>();
        Set<Long> ids2Get = new HashSet<Long>(ids);

        // 此处先进行thread local查询
        Map<Long, T> data = getModelFromThreadlocal(ids2Get);
        result.putAll(data);
        ids2Get.removeAll(data.keySet());
        ids2Get.removeAll(getDelModelFromThreadlocal(ids2Get).keySet());

        if (ids2Get.isEmpty()) {
            return result;
        }

        Map<Long, Map<R, Object>> modelFields = getModelField(ids, fields);

        return fillModel(modelFields);
    }


    public Map<Long, Map<R, Object>> getModelField(Set<Long> ids, Set<R> fields) {
        Set<Long> ids2Get = new HashSet<Long>(ids);
        Map<Long, Map<R, Object>> result = new LinkedHashMap<Long, Map<R, Object>>();
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(fields)) {
            return result;
        }
        Map<Long, Map<R, Object>> dbData = getModelFieldFromDB(ids2Get, fields);
        return dbData;
    }

    public Map<Long, T> getModelByCondition(Long optid, Map<R, Object> conditions, Set<R> fields) {

        Map<Long, Map<R, Object>> modelFields = getModelFieldFromDB(conditions, fields);
        Set<Long> delIds = Sets.newHashSet();
        for (Long key : modelFields.keySet()) {
        	delIds.addAll(getDelModelFromThreadlocal(Sets.newHashSet(key)).keySet());
        }
        for (Long delId : delIds) {
        	modelFields.remove(delId);
        }
        return fillModel(modelFields);
    }


    @Transactional(rollbackFor = Exception.class)
    @DBOperationDesc(db = "WRITE")
    public Map<Long, T> saveModel(Long opuid, Map<Long, ModelPair<T, R>> models) {

        Map<Long, Map<R, Object>> modelid2saver = new HashMap<Long, Map<R, Object>>();
        Map<Long, T> oldModels = new HashMap<Long, T>();
        Map<Long, T> newModels = new HashMap<Long, T>();

        for (Long id : models.keySet()) {
            ModelPair<T, R> modelPair = models.get(id);
            T oldModel = modelPair.getOldModel();
            T newModel = modelPair.getNewModel();
            oldModels.put(id, oldModel);
            newModels.put(id, newModel);
            newModel.setContainer(id);
            oldModel.setContainer(id);
            if (oldModel != null && newModel != null) {
                Map<R, Object> diff = modelPair.getDiffModifyField();
                modelid2saver.put(id, diff);
            }
        }

        saveData2DB(opuid, modelid2saver, oldModels, newModels);

        // 更新缓存
        // 分field暂时不更新
        // saveModel2Cache(newModels);

        publishSaveEvent(opuid, oldModels, newModels, modelid2saver);

        return newModels;
    }

    /**
     * @param ids
     * @param fields
     * @return
     */
    protected abstract Map<Long, Map<R, Object>> getModelFieldFromDB(Set<Long> ids, Set<R> fields);

    protected abstract Map<Long, Map<R, Object>> getModelFieldFromDB(Map<R, Object> conditions, Set<R> fields);

    @DBOperationDesc(db = "WRITE")
    @Transactional(rollbackFor = Exception.class)
    public Map<Long, T> addModel(Long optid, Map<Long, T> id2model) {

        Map<Long, T> result = new HashMap<Long, T>();
     // 修一个add方法后索引值改变的坑，如果改所有repository代码代价较大，这个通用处理下。
        Map<Long, T> addResult = new HashMap<Long, T>(); 
        Map<Long, Long> mapping = Maps.newHashMap();
        if (id2model == null || id2model.isEmpty()) {
            return result;
        }

        List<T> addModels = new ArrayList<T>();
        long index = 0l;
        for (Long key : id2model.keySet()) {
        	T model = id2model.get(key);
            model.setContainer(model.getId());
            addModels.add(model);
            mapping.put(index++, key);
        }

        addResult = addModel2DB(optid, addModels);
        for (Long i : addResult.keySet()) {
        	T t = addResult.get(i);
        	Long key = mapping.get(i);
        	if (key != null) {
        		result.put(key, t);
        	} else {
        		result.put(i, t);
        	}
        }

        saveModel2Cache(result);

        publishAddEvent(optid, result);

        return result;
    }

    protected void mergeField(R field, Map<Long, T> id2model, Map<Long, Object> subModel) throws Exception {
        for (Long id : id2model.keySet()) {
            T model = id2model.get(id);
            PropertyUtils.setProperty(model, field.toString(), subModel.get(id));
        }
    }

    @DBOperationDesc(db = "WRITE")
    @Transactional(rollbackFor = Exception.class)
    public void delModelById(Long optid, Map<Long, T> id2model) {

        delModel2DB(optid, id2model);

        // 从threadlocal中删除model
        removeFromThreadLocal(id2model);

        publishDelEvent(optid, id2model);

    }

    protected boolean needPublish() {
        return true;
    }

    protected void publishDelEvent(Long opuid, Map<Long, T> id2model) {
        if (needPublish()) {
        	ModelDelEvent<T> event = new ModelDelEvent<T>(opuid, id2model, getModelType());
        	EventHolder.getThreadEvents().add(event);
        	applicationContext.publishEvent(event);
        }
    }

    protected void publishAddEvent(Long opuid, Map<Long, T> newModels) {
    	ModelAddEvent<T> event = new ModelAddEvent<T>(opuid, newModels, getModelType());
    	EventHolder.getThreadEvents().add(event);
        applicationContext.publishEvent(event);
    }

    protected void publishSaveEvent(Long opuid, Map<Long, T> oldmodels, Map<Long, T> newmodels,
                                    Map<Long, Map<R, Object>> modelid2saver) {
        if (needPublish()) {
        	ModelSaveEvent<T, R> event = new ModelSaveEvent<T, R>(opuid, oldmodels, newmodels, modelid2saver, getModelType());
        	EventHolder.getThreadEvents().add(event);
        	applicationContext.publishEvent(event);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        columnProcessor = processorContext.getConverter(getModelType().name());
        this.modelContext = columnProcessor.getModelContext();
        super.afterPropertiesSet();
    }


    protected class OptionItem<X extends Enum<X>> {

        private List<Pair<X, Boolean>> orderBys;

        private Integer num;

        private Integer offset;

        public List<Pair<X, Boolean>> getOrderBys() {
            return orderBys;
        }

        public void setOrderBys(List<Pair<X, Boolean>> orderBys) {
            this.orderBys = orderBys;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }
    }

    private <X extends Enum<X>> void refreshLimit(OptionItem<X> optionItem, OptionLimit limit) {
        Integer[] lim = limit.getLimit();
        if (lim != null) {
            if (lim.length > 1) {
                optionItem.setNum(lim[1]);
                optionItem.setOffset(lim[0]);
            } else {
                optionItem.setNum(lim[0]);
            }
        }
    }

    private <X extends Enum<X>> void refreshOrderBy(OptionItem<X> optionItem, OptionOrderby orderby, Class<X> enumClazz) {
        List<Pair<X, Boolean>> orderbys = new ArrayList<Pair<X, Boolean>>();
        Map<String, Boolean> map = orderby.getOption();
        if (map != null && !map.isEmpty()) {
            for (String field : map.keySet()) {
                Pair<X, Boolean> pair = new MutablePair(X.valueOf(enumClazz, field.toString()), map.get(field));
                orderbys.add(pair);
            }
        }
        optionItem.setOrderBys(orderbys);
    }

    protected <X extends Enum<X>> OptionItem<X> convertOption(Option[] options, Class<X> enumClazz) {
        OptionItem<X> optionItem = new OptionItem<X>();
        if (options == null || options.length <= 0) {
            return optionItem;
        }

        for (Option option : options) {
            if (option instanceof OptionLimit) {
                refreshLimit(optionItem, (OptionLimit) option);
            }

            if (option instanceof OptionOrderby) {
                refreshOrderBy(optionItem, (OptionOrderby) option, enumClazz);
            }

        }

        return optionItem;
    }

    protected <X extends Enum<X>> Map<X, Object> buildConditions(List<ConditionPair<C>> conditionPairs, Class<X> enumClazz) {
        Map<X, Object> conditions = new HashMap<X, Object>();
        if (conditionPairs != null) {
        	Map<String, Integer> conditionFieldCnt = Maps.newHashMap();
        	for (ConditionPair<C> conditionPair : conditionPairs) {
        		C c = conditionPair.getField();
        		if (conditionFieldCnt.containsKey(c.toString())) {
        			conditionFieldCnt.put(c.toString(), conditionFieldCnt.get(c.toString()) + 1);
        		} else {
        			conditionFieldCnt.put(c.toString(), 1);
        		}
        	}
            for (ConditionPair<C> conditionPair : conditionPairs) {
                C conditionField = conditionPair.getField();
                X x = X.valueOf(enumClazz, conditionField.toString());
                ConditionPair<BoColumn> conditionP = new ConditionPair<BoColumn>(conditionPair.getFunc(), (BoColumn) x, conditionPair.getValue());
                if (!conditions.containsKey(x)) {
                	if (conditionFieldCnt.get(conditionField.toString()) > 1) {
                		MultiConditionPair multiConditionPair = new MultiConditionPair();
                		multiConditionPair.getConditionPairs().add(conditionP);
                		conditions.put(x, multiConditionPair);
                	} else {
                		conditions.put(x, conditionP);
                	}
                } else {
                	if (conditionFieldCnt.get(conditionField.toString()) > 1) {
                		((MultiConditionPair) conditions.get(x)).getConditionPairs().add(conditionP);
                	}
                }
            }
        }
        return conditions;
    }

    @Override
    public Long getModelCountByCondition(Long optid, Map<R, Object> conditions) {
        return 0L;
    }

    @Override
    public List<Map<String, Object>> getModelGroupByByCondition(Long optid, Map<R, Object> conditions, Set<R> groupByFields, Map<GroupFunc, R> groupFuncMap) {
        return Lists.newArrayList();
    }

    protected void refreshDict(Set<R> fields4Query, Map<Long, Map<R, Object>> result) {
        // 字典表查询.
        for (FieldContext fieldContext : modelContext.getRefDictField()) {
            R dict = columnProcessor.convert(fieldContext.getName());
            R dictId = columnProcessor.convert(fieldContext.getRefDict2Dictid().get(fieldContext.getName()));
            AbstractDictDao dao = applicationContext.getBean(fieldContext.getDictDao(), AbstractDictDao.class);

            if (StringUtils.isNotBlank(fieldContext.getRefExposeField())) {
                R expose = columnProcessor.convert(fieldContext.getRefExposeField());
                DictStoreType storeType = fieldContext.getFieldOption().getDictJoiner() ? DictStoreType.joiner : DictStoreType.mask;
                if (fields4Query.contains(expose)) {
                    Map<Long, Long[]> model4refExpose = new HashMap<Long, Long[]>();
                    for (Long id : result.keySet()) {
                        Object field = result.get(id).get(dictId);
                        if (field==null || ((field instanceof String) && StringUtils.isBlank((String)field))) {
                            continue;
                        }
                        model4refExpose.put(id, dictSplitter(field, storeType));
                    }
                    MergeUtils.mergeSortedItems(result, model4refExpose, expose);
                }
            }

            if (fields4Query.contains(dict)) {
                if (fieldContext.hasOption() && (fieldContext.getFieldOption().getDictJoiner() || fieldContext.getFieldOption().getDictMask())) {

                    DictStoreType storeType = fieldContext.getFieldOption().getDictJoiner() ? DictStoreType.joiner : DictStoreType.mask;
                    Map<Long, Object> dictMap = new HashMap<Long, Object>();
                    Map<Long, Long[]> model4refExpose = new HashMap<Long, Long[]>();
                    for (Long id : result.keySet()) {
                        Object field = result.get(id).get(dictId);
                        if (field==null || ((field instanceof String) && StringUtils.isBlank((String)field))) {
                            continue;
                        }
                        dictMap.put(id, field);
                        model4refExpose.put(id, dictSplitter(field, storeType));
                    }

                    Map<Long, String[]> bookTagDictMap = dao.getDict(dictMap, storeType);
                    MergeUtils.mergeSortedItems(result, bookTagDictMap, dict);
                    MergeUtils.mergeSortedItems(result, model4refExpose, columnProcessor.convert(fieldContext.getRefExposeField()));
                } else {
                    Map<Long, Long> dictMap = new HashMap<Long, Long>();
                    for (Long id : result.keySet()) {
                        if (result.get(id).get(dictId) == null) {
                            continue;
                        }
                        dictMap.put(id, ((Number) result.get(id).get(dictId)).longValue());
                    }
                    Map<Long, String> queryDictMap = dao.getDict(dictMap);
                    MergeUtils.mergeSortedItems(result, queryDictMap, dict);
                }
            }
        }

    }

    protected Long[] dictSplitter(Object field, DictStoreType storeType) {
        if (storeType == DictStoreType.joiner) {
            return dictJoinerSplitter((String)field);
        } else if (storeType == DictStoreType.mask) {
            return dictMaskSplitter((String)field);
        } else {
        	return null;
        }
        
    }

    protected Long[] dictMaskSplitter(String field) {
        List<Long> ids = new ArrayList<Long>();
//        long mask =1;
        BigInteger mask = new BigInteger("1");
        for (long i=1; i<=130;i++) {
            if (i>1) {
            	mask = mask.shiftLeft(1);
//                mask = mask << 1;
            }
            if (mask.and(new BigInteger(field)).equals(mask)) {
//            if ((field & mask)== mask) {
                ids.add(i);
            }
        }
        return ids.toArray(new Long[]{});
    }

    protected Long[] dictJoinerSplitter(String field) {
        if (StringUtils.isBlank(field)) {
            return new Long[]{};
        }
        List<String> strIds = Splitter.on(",").trimResults().splitToList(field);
        List<Long> ids = new ArrayList<Long>();
        for (String sId : strIds) {
            ids.add(Long.parseLong(sId));
        }
        return ids.toArray(new Long[]{});
    }
}
