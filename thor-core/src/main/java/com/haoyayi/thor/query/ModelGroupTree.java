package com.haoyayi.thor.query;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.ConditionFunc;
import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.GroupFunc;
import com.haoyayi.thor.api.Option;
import com.haoyayi.thor.processor.ProcessorContext;

public class ModelGroupTree extends ModelTree {

	private Set<BaseTypeField> groupFields = Sets.newHashSet();
	private Map<GroupFunc, BaseTypeField> groupFuncs = Maps.newHashMap();
	private List<Map<String, Object>> groupResults = Lists.newArrayList();
	
	public ModelGroupTree rootOf(String model, ProcessorContext processorContext) {
		super.rootOf(model, processorContext);
		return this;
	}
	
	public void with(ConditionPair<ConditionField>[] conditions, Set<BaseTypeField> groupFields, Map<GroupFunc, BaseTypeField> groupFuncs) {
		withConditions(conditions);
		this.groupFields = groupFields;
		this.groupFuncs = groupFuncs;
	}
	
	public List<Map<String, Object>> queryGroup() {
		if (hasError.get()) {
			return groupResults;
		}
		queryGroupByConditions();
		return groupResults;
	}
	
	private Optional<Set<Long>> queryGroupByConditions() {
		AbstractQueryFacade<?, BaseTypeField, ConditionField> facade = getQueryFacade(name);
		if (CollectionUtils.isNotEmpty(subModels)) {
			for (ModelTree subModel : subModels) {
				Optional<Set<Long>> queryOptional = subModel.queryByConditions();
				if (queryOptional.isPresent()) {
					Set<Long> subResult = queryOptional.get();
					if (CollectionUtils.isEmpty(subResult)) {
						// 子model根据条件没有查询到任何数据，终止查询
						Set<Long> set = Sets.newHashSet();
						return Optional.of(set);
					}
					// 子model的结果作为条件，融合到父model的condition中
					Set<Long> queryResult = queryOptional.get();
					if (subModel.isOneToMany()) {
						ConditionPair<ConditionField> newConditionPair = new ConditionPair<ConditionField>();
						newConditionPair.setField(cp.getPkField());
						newConditionPair.setFunc(ConditionFunc.IN);
						newConditionPair.setValue(queryResult.toArray());
						conditions.add(newConditionPair);
					} else {
						String subModelRefId = cp.getSubModelRefId(subModel.getName());
						ConditionPair<ConditionField> newConditionPair = new ConditionPair<ConditionField>();
						newConditionPair.setField(cp.convert(subModelRefId));
						newConditionPair.setFunc(ConditionFunc.IN);
						newConditionPair.setValue(queryResult.toArray());
						conditions.add(newConditionPair);
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(conditions)) {
			// 剔除掉SUBIN得条件
			for (ConditionPair<ConditionField> conditionPair : conditions) {
				if (conditionPair.getFunc() != ConditionFunc.SUBIN) {
					noSubConditions.add(conditionPair);
				}
			}
		}
		if (isRoot) {
			groupResults = facade.queryGroup(name, noSubConditions, groupFields, groupFuncs);
			return null;
		}
		if (CollectionUtils.isNotEmpty(noSubConditions)) {
			// 只查询主键id
			Map<Long,Map<BaseTypeField,Object>> queryResult = facade.query(Sets.newHashSet(cp.getPkField()), noSubConditions, new Option[0]);
			this.queryIds = queryResult.keySet();
			return Optional.of(queryIds);
		} else {
			return Optional.absent();
		}
	}

}
