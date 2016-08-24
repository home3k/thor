package com.haoyayi.thor.query;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.ConditionFunc;
import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.Option;
import com.haoyayi.thor.processor.ProcessorContext;

public class ModelCntTree extends ModelTree {

//	private static ProcessorContext processorContext;
	
//	private String name;
//	private Long optid;
//	private Boolean isRoot = false;
//	private Set<BaseTypeField> fields =Sets.newHashSet();
//	private List<ConditionPair<ConditionField>> conditions = Lists.newArrayList();
//	private List<ConditionPair<ConditionField>> noSubConditions = Lists.newArrayList();
//	private Set<ModelCntTree> subModels = Sets.newHashSet();
//	private Set<Long> queryIds;
//	private static ThreadLocal<Boolean> hasError = new ThreadLocal<Boolean>();
//	private ColumnProcessor<BaseTypeField> cp;
	private Long cnt = 0l;
	
	public ModelCntTree rootOf(String model, Long optid, ProcessorContext processorContext) {
		super.rootOf(model, optid, processorContext); 
		return this;
	}
	
	public void with(ConditionPair<ConditionField>[] conditions) {
		withConditions(conditions);
	}
	
	public Long queryCnt() {
		if (hasError.get()) {
			return cnt;
		}
		queryCntByConditions();
		return cnt;
	}
	
	private Optional<Set<Long>> queryCntByConditions() {
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
			cnt = facade.queryCnt(optid, name, noSubConditions);
			return null;
		}
		if (CollectionUtils.isNotEmpty(noSubConditions)) {
			// 只查询主键id
			Map<Long,Map<BaseTypeField,Object>> queryResult = facade.query(optid, Sets.newHashSet(cp.getPkField()), noSubConditions, new Option[0]);
			this.queryIds = queryResult.keySet();
			return Optional.of(queryIds);
		} else {
			return Optional.absent();
		}
	}
	

//	private AbstractQueryFacade<?, BaseTypeField, ConditionField> getQueryFacade(String model) {
//		AbstractQueryFacade<?, BaseTypeField, ConditionField> facade = (AbstractQueryFacade<?, BaseTypeField, ConditionField>) processorContext.getQuery(model);
//		return facade;
//	}
	
//	private ModelCntTree withConditions(ConditionPair<ConditionField>[] conditions) {
//		if (conditions == null || conditions.length == 0) {
//            return this;
//        }
//		// 为避免condition对象field转换对后续查询造成影响
//		ConditionPair<ConditionField>[] cloneConditions = new ConditionPair[conditions.length];
//		for (int i = 0; i < conditions.length; i++) {
//			cloneConditions[i] = conditions[i].clone();
//		}
//        for (ConditionPair<ConditionField> condition : cloneConditions) {
//            String field = condition.getField().toString();
//            if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
//                List<String> splitFields = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
//                withFields(Sets.newHashSet(field));
//                withConditions0(splitFields.iterator(), this, condition);
//            } else {
//                this.conditions.add(condition);
//                ConditionValidator<ConditionField> validator = processorContext.getConditionValidator(name);
//                CheckResult<List<ConditionPair<ConditionField>>> validateResult = validator.validate(optid, this.conditions);
//                if (validateResult.isErrorResult()) {
//                	hasError.set(true);
//                }
//            }
//        }
//		return this;
//	}
	
//	private void withConditions0(Iterator<String> fields, ModelCntTree parent, ConditionPair<ConditionField> condition) {
//		String field = fields.next();
//		if (fields.hasNext()) {
//			ModelCntTree subType = getSubModel(parent, field);
//			withConditions0(fields, subType, condition);
//		} else {
//			condition.setField(processorContext.getConverter(parent.getName()).convert(field));
//			parent.getConditions().add(condition);
//			ConditionValidator<ConditionField> validator = processorContext.getConditionValidator(parent.getName());
//            CheckResult<List<ConditionPair<ConditionField>>> validateResult = validator.validate(optid, parent.getConditions());
//            if (validateResult.isErrorResult()) {
//            	hasError.set(true);
//            }
//		}
//	}
	
//	private ModelCntTree withFields(Set<String> fields) {
//		for (String field : fields) {
//			if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
//				List<String> model2field = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
//				withFields0(model2field.iterator(), this);
//			} else {
//				addFields(cp.convert(field));
//			}
//		}
//		return this;
//	}
//	
//	private void withFields0(Iterator<String> fields, ModelCntTree parent) {
//		String field = fields.next();
//		if (fields.hasNext()) {
//			ModelCntTree type = getSubModel(parent, field);
//			if (type == null) {
//				type = new ModelCntTree();
//				type.setName(field);
//				type.setCp(getQueryFacade(field).getColumnProcessor());
//			}
//			ColumnProcessor<BaseTypeField> cp = parent.getCp();
//			parent.getSubModels().add(type);
//			parent.addFields(cp.convert(cp.getSubModelRefId(field)));
//			withFields0(fields, type);
//		} else {
//			parent.getFields().add(parent.getCp().convert(field));
//		}
//	}
	
//	private void addFields(BaseTypeField field) {
////		ColumnProcessor<BaseTypeField> cp = getQueryFacade(name).getColumnProcessor();
//		fields.add(field);
//		for (BaseTypeField otherField : cp.getOtherNecessaryFields(field)) {
//			fields.add(otherField);
//		}
//	}
	
//	private ModelCntTree getSubModel(ModelCntTree model, String name) {
//		for (ModelCntTree subType : model.getSubModels()) {
//			if (subType.getName().equals(name)) {
//				return subType;
//			}
//		}
//		return null;
//	}


}
