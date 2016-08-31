package com.haoyayi.thor.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haoyayi.thor.context.meta.FieldContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.haoyayi.thor.api.BaseType;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ConditionField;
import com.haoyayi.thor.api.ConditionFunc;
import com.haoyayi.thor.api.ConditionPair;
import com.haoyayi.thor.api.Option;
import com.haoyayi.thor.api.OptionLimit;
import com.haoyayi.thor.api.OptionOrderby;
import com.haoyayi.thor.common.CheckResult;
import com.haoyayi.thor.constants.ModelConstants;
import com.haoyayi.thor.processor.ColumnProcessor;
import com.haoyayi.thor.processor.ProcessorContext;
import com.haoyayi.thor.validate.ConditionValidator;

@SuppressWarnings("unchecked")
public class ModelTree {

	protected static ProcessorContext processorContext;
	
	protected String name;
	protected Long optid;
	protected Boolean isRoot = false;
	protected Set<BaseTypeField> fields =Sets.newHashSet();
	protected List<ConditionPair<ConditionField>> conditions = Lists.newArrayList();
	protected List<ConditionPair<ConditionField>> noSubConditions = Lists.newArrayList();
//	private Option<BaseType<BaseTypeField>>[] limitOptions;
	protected Option<BaseType<BaseTypeField>>[] orderbyOptions;
	protected Option<BaseType<BaseTypeField>>[] options;
	protected boolean isOneToMany;
	protected boolean isMapping;
	protected ModelTree parent;
	protected Set<ModelTree> subModels = Sets.newHashSet();
	protected Set<Long> queryIds;
	protected Map<Long, Map<BaseTypeField, Object>> queryResult = Maps.newHashMap();
	protected static ThreadLocal<Boolean> hasError = new ThreadLocal<Boolean>();
	protected ColumnProcessor<BaseTypeField> cp;
	protected AbstractQueryFacade<?, BaseTypeField, ConditionField> facade; 
	
	public ModelTree rootOf(String model, ProcessorContext processorContext) {
		if (processorContext != null) {
			ModelTree.processorContext = processorContext;
		}
		name = model;
		this.optid = optid;
		isRoot = true;
		hasError.set(false);
		cp = processorContext.getConverter(name);
		facade = getQueryFacade(name);
		return this;
	}
	
	public void with(Set<String> fields, ConditionPair<ConditionField>[] conditions, Option<BaseType<BaseTypeField>>[] options) {
		withFields(fields);
		withConditions(conditions);
		withOptions(options);
	}
	
	public <T>Map<Long, T> query() {
		Map<Long, T> result = Maps.newLinkedHashMap();
		if (hasError.get()) {
			return result;
		}
		queryByConditions();
		queryFields(queryIds);
		merge();
		Map<Long,BaseType<BaseTypeField>> origin = ((AbstractQueryFacade<BaseType<BaseTypeField>, BaseTypeField, ConditionField>) facade).render(fillModel());
		for (Long key : origin.keySet()) {
        	result.put(key, (T) origin.get(key));
        }
		return result;
	}
	
	protected Optional<Set<Long>> queryByConditions() {
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
		if (CollectionUtils.isNotEmpty(noSubConditions) || isRoot) {
			if (isOneToMany()) {
				BaseTypeField conditionField = null;
				if (cp.getSubModelRefId(parent.getName()) != null) {
					conditionField = cp.convert(cp.getSubModelRefId(parent.getName()));
				} else {
					FieldContext oneToManyField = getParent().getCp().getModelContext().getFieldContext(name);
					conditionField = cp.convert(oneToManyField.getRefModelField4ModelField().keySet().iterator().next());
				}
				Map<Long,Map<BaseTypeField,Object>> queryResult = facade.query(Sets.newHashSet(conditionField), noSubConditions, options);
				Set<Long> queryIds = Sets.newHashSet();
				for (Map<BaseTypeField, Object> map : queryResult.values()) {
					queryIds.add((Long) map.get(conditionField));
				}
				this.queryIds = queryIds;
			} else {
				Map<Long,Map<BaseTypeField,Object>> queryResult = facade.query(Sets.newHashSet(cp.getPkField()), noSubConditions, options);
				this.queryIds = queryResult.keySet();
			}
			// 只查询主键id
			return Optional.of(queryIds);
		} else {
			return Optional.absent();
		}
	}
	
	private void queryFields(Set<Long> ids) {
		if (isOneToMany) {
			ConditionPair<ConditionField> IdConditionPair = new ConditionPair<ConditionField>();
			if (cp.getSubModelRefId(parent.getName()) != null) {
				IdConditionPair.setField(cp.convert(cp.getSubModelRefId(parent.getName())));
			} else {
				FieldContext oneToManyField = getParent().getCp().getModelContext().getFieldContext(name);
				IdConditionPair.setField(cp.convert(oneToManyField.getRefModelField4ModelField().keySet().iterator().next()));
			}
			IdConditionPair.setFunc(ConditionFunc.IN);
			IdConditionPair.setValue(ids.toArray());
			conditions.add(IdConditionPair);
		} else {
			if (CollectionUtils.isEmpty(ids)) {
				return;
			}
			// 依次通过id进行查询，原来的condition的id条件删除掉
//			this.conditions.clear();
//			for (Iterator<ConditionPair<ConditionField>> i = conditions.iterator(); i.hasNext();) {
//				ConditionPair<ConditionField> con = i.next();
//				if (con.getField().toString().equals(cp.getPkField().toString())) {
//					i.remove();
//				}
//			}
			ConditionPair<ConditionField> IdConditionPair = new ConditionPair<ConditionField>();
			IdConditionPair.setField(cp.getPkField());
			IdConditionPair.setFunc(ConditionFunc.IN);
			IdConditionPair.setValue(ids.toArray());
			conditions.add(IdConditionPair);
			fields.add(cp.getPkField());
		}
		this.queryResult = facade.query(fields, conditions, orderbyOptions);
		if (MapUtils.isEmpty(this.queryResult)) {
			return;
		}
		for (ModelTree subModel : subModels) {
			Set<Long> subModelIds = Sets.newHashSet();
			if (subModel.isOneToMany()) {
				BaseTypeField subModelRefId = cp.getPkField();
//				String subModelRefId = subModel.getCp().getSubModelPk(name);
				for (Map<BaseTypeField,Object> modelFields : this.queryResult.values()) {
					subModelIds.add((Long) modelFields.get(subModelRefId));
				}
			} else {
				String subModelRefId = cp.getSubModelRefId(subModel.getName());
				for (Map<BaseTypeField,Object> modelFields : this.queryResult.values()) {
					subModelIds.add((Long) modelFields.get(cp.convert(subModelRefId)));
				}
			}
			subModel.queryFields(subModelIds);
		}
	}

	protected AbstractQueryFacade<?, BaseTypeField, ConditionField> getQueryFacade(String model) {
		AbstractQueryFacade<?, BaseTypeField, ConditionField> facade = (AbstractQueryFacade<?, BaseTypeField, ConditionField>) processorContext.getQuery(model);
		return facade;
	}
	
	private Map<Long,Map<BaseTypeField,Object>> merge() {
		if (MapUtils.isEmpty(queryResult)) {
			return Maps.newHashMap();
		}
		for (ModelTree subModel : subModels) {
			Map<Long,Map<BaseTypeField,Object>> subModelFields = subModel.merge();
			if (subModel.isMapping()) {
				FieldContext mappingSubModelField = cp.getModelContext().getFieldContextWithMapping(subModel.getName());
				BaseTypeField mainIdField = subModel.getCp().convert(mappingSubModelField.getRefMappingField2MainModelField().get(subModel.getName()));
				for (Map<BaseTypeField, Object> map : subModelFields.values()) {
					Object mainId = map.get(mainIdField);
					if (queryResult.containsKey(mainId)) {
						// 找到此次查询中影射关系的主model
						Map<BaseTypeField, Object> mainModelMap = queryResult.get(mainId);
						Object mappingSubModel = map.get(subModel.getCp().convert(mappingSubModelField.getSubModelField2subModelType().get(mappingSubModelField.getName())));
						if (mappingSubModel instanceof Map && MapUtils.isNotEmpty((Map<Object, Object>) mappingSubModel)) {
							if (!mainModelMap.containsKey(cp.convert(mappingSubModelField.getName()))) {
								// 多对多的主model记录着子model的数组
								mainModelMap.put(cp.convert(mappingSubModelField.getName()), Lists.newArrayList());
							}
							((Collection<Object>) mainModelMap.get(cp.convert(mappingSubModelField.getName()))).add(mappingSubModel);
						}
					}
				}
			} else if (subModel.isOneToMany()) {
				BaseTypeField subModelField = cp.convert(subModel.getName());
				for (Long pk : queryResult.keySet()) {
					Map<BaseTypeField,Object> modelFields = queryResult.get(pk);
					List<Map<BaseTypeField,Object>> thisSubModelFields = (List<Map<BaseTypeField,Object>>) modelFields.get(subModelField);
					if (thisSubModelFields == null) {
						thisSubModelFields = Lists.newArrayList();
						modelFields.put(subModelField, thisSubModelFields);
					}
					for (Map<BaseTypeField, Object> map : subModelFields.values()) {
						BaseTypeField field = null;
						if (subModel.getCp().getSubModelRefId(name) != null) {
							field = subModel.getCp().convert(subModel.getCp().getSubModelRefId(name));
						} else {
							FieldContext subFieldContext = cp.getModelContext().getFieldContext(subModel.getName());
							field = subModel.getCp().convert(subFieldContext.getRefModelField4ModelField().keySet().iterator().next());
						}
						if (pk.equals(map.get(field))) {
							thisSubModelFields.add(map);						
						}
					}
				}
			} else {
				BaseTypeField subModelField = cp.convert(subModel.getName());
				BaseTypeField subModelRefId = cp.convert(cp.getSubModelRefId(subModel.getName()));
				for (Map<BaseTypeField,Object> modelFields: queryResult.values()) {
					Map<BaseTypeField,Object> thisSubModelFields = Maps.newHashMap();
					modelFields.put(subModelField, thisSubModelFields);
					Long subModelId = (Long) modelFields.get(subModelRefId);
					if (subModelFields.containsKey(subModelId)) {
						thisSubModelFields.putAll(subModelFields.get(subModelId));
					}
				}
			}
		}
		facade.renderMapType(queryResult);
		return queryResult;
	}
	
	private Map<Long, BaseType<BaseTypeField>> fillModel() {
		return processorContext.getModelRepository(name).fillModel(queryResult);
	}
	
	protected ModelTree withConditions(ConditionPair<ConditionField>[] conditions) {
		if (conditions == null || conditions.length == 0) {
            return this;
        }
		// 为避免condition对象field转换对后续查询造成影响
		ConditionPair<ConditionField>[] cloneConditions = new ConditionPair[conditions.length];
		for (int i = 0; i < conditions.length; i++) {
			cloneConditions[i] = conditions[i].clone();
		}
        for (ConditionPair<ConditionField> condition : cloneConditions) {
            String field = condition.getField().toString();
            if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
                List<String> splitFields = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
                withFields(Sets.newHashSet(field));
                withConditions0(null, splitFields.iterator(), this, condition);
            } else {
                this.conditions.add(condition);
                ConditionValidator<ConditionField> validator = processorContext.getConditionValidator(cp.getModelContext().getName());
                CheckResult<List<ConditionPair<ConditionField>>> validateResult = validator.validate(this.conditions);
                if (validateResult.isErrorResult()) {
                	hasError.set(true);
                }
            }
        }
		return this;
	}
	
	private void withConditions0(String _field, Iterator<String> fields, ModelTree parent, ConditionPair<ConditionField> condition) {
		String field = StringUtils.isNotEmpty(_field) ? _field : fields.next();
		if (parent.getCp().isMappingSubModelField(field)) {
			FieldContext fc = parent.getFacade().getColumnProcessor().getModelContext().getFieldContext(field);
			ModelTree subType = getSubModel(parent, fc.getRefMappingModel());
			withConditions0(fc.getSubModelField2subModelType().get(field), fields, subType, condition);
		} else {
			if (fields.hasNext()) {
				ModelTree subType = getSubModel(parent, field);
				withConditions0(null, fields, subType, condition);
			} else {
				condition.setField(parent.getCp().convert(field));
				parent.getConditions().add(condition);
				ConditionValidator<ConditionField> validator = processorContext.getConditionValidator(parent.getCp().getModelContext().getName());
				CheckResult<List<ConditionPair<ConditionField>>> validateResult = validator.validate(optid, parent.getConditions());
				if (validateResult.isErrorResult()) {
					hasError.set(true);
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private ModelTree withOptions(Option[] options) {
		List<Option> orderbyOptions = Lists.newArrayList();
		List<Option> limitOptions = Lists.newArrayList();
		for (Option option : options) {
			if (option instanceof OptionOrderby) {
				if (MapUtils.isNotEmpty(((OptionOrderby) option).getOrderby())) {
					orderbyOptions.add(option);
				}
			}
			if (option instanceof OptionLimit) {
				limitOptions.add(option);
			}
		}
		this.orderbyOptions = CollectionUtils.isEmpty(orderbyOptions) ? null : orderbyOptions.toArray(new Option[0]);
//		this.limitOptions = CollectionUtils.isEmpty(limitOptions) ? null : limitOptions.toArray(new Option[0]);
		List<Option> optionList = Lists.newArrayList();
		optionList.addAll(orderbyOptions);
		optionList.addAll(limitOptions);
		this.options = optionList.toArray(new Option[0]);
		return this;
	}
	
	private ModelTree withFields(Set<String> fields) {
		for (String field : fields) {
			if (field.contains(ModelConstants.MODEL_QUERY_SEP)) {
				List<String> model2field = Splitter.on(ModelConstants.MODEL_QUERY_SEP).splitToList(field);
				withFields0(null, model2field.iterator(), this);
			} else {
				addFields(cp.convert(field));
			}
		}
		return this;
	}
	
	private void withFields0(String _field, Iterator<String> fields, ModelTree parent) {
		String field = StringUtils.isNotEmpty(_field) ? _field : fields.next();
		// 多对多子model
		if (parent.getCp().isMappingSubModelField(field)) {
			parent.setOneToMany(false);
			// 先构建中间表对象
			FieldContext fc = parent.getFacade().getColumnProcessor().getModelContext().getFieldContext(field);
			ModelTree type = getSubModel(parent, fc.getRefMappingModel());
			if (type == null) {
				type = new ModelTree();
				type.setName(fc.getRefMappingModel());
				if (fc != null) {
					type.setParent(parent);
					type.setFacade(getQueryFacade(fc.getRefMappingModel()));
					type.setOneToMany(true);
					type.setMapping(true);
					type.setCp(type.getFacade().getColumnProcessor());
					type.addFields(type.getCp().convert(fc.getRefMappingField2MainModelField().get(fc.getRefMappingModel())));
					type.addFields(type.getCp().convert(fc.getRefMappingField2SubModelField().get(fc.getRefMappingModel())));
					parent.addFields(parent.getCp().convert(type.getCp().getSubModelPk(parent.getName())));
				}
			}
			parent.getSubModels().add(type);
			// field的别名替换。
			withFields0(fc.getSubModelField2subModelType().get(field), fields, type);
		} else {
			if (fields.hasNext()) {
				ModelTree type = getSubModel(parent, field);
				if (type == null) {
					type = new ModelTree();
					type.setName(field);
					for (FieldContext fc : parent.getFacade().getColumnProcessor().getModelContext().getField()) {
						if (fc.getName().equals(field)) {
							type.setParent(parent);
							type.setFacade(getQueryFacade(fc.getRefModel()));
							type.setOneToMany(isOneToMany(fc));
							type.setCp(type.getFacade().getColumnProcessor());
							if (type.isOneToMany()) {
								type.addFields(type.getCp().convert(fc.getRefModelField4ModelField().keySet().iterator().next()));
//								parent.addFields(parent.getCp().convert(type.getCp().getSubModelPk(parent.getName())));
								parent.addFields(parent.getCp().getPkField());
							}
							break;
						}
					}
				}
				ColumnProcessor<BaseTypeField> parentCp = parent.getCp();
				parent.getSubModels().add(type);
				if (!type.isOneToMany()) {
					parent.addFields(parentCp.convert(parentCp.getSubModelRefId(field)));
				}
				withFields0(null, fields, type);
			} else {
				parent.addFields(parent.getCp().convert(field));
			}
		}
	}
	
	private void addFields(BaseTypeField field) {
//		ColumnProcessor<BaseTypeField> cp = getQueryFacade(name).getColumnProcessor();
		fields.add(field);
		for (BaseTypeField otherField : cp.getOtherNecessaryFields(field)) {
			fields.add(otherField);
		}
	}
	
	private ModelTree getSubModel(ModelTree model, String name) {
		for (ModelTree subType : model.getSubModels()) {
			if (subType.getName().equals(name)) {
				return subType;
			}
		}
		return null;
	}

	private boolean isOneToMany(FieldContext fc) {
		return fc.getMeta().contains("@refsmodel");
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<BaseTypeField> getFields() {
		return fields;
	}

	public void setFields(Set<BaseTypeField> fields) {
		this.fields = fields;
	}

	public List<ConditionPair<ConditionField>> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionPair<ConditionField>> conditions) {
		this.conditions = conditions;
	}

	private Set<ModelTree> getSubModels() {
		return subModels;
	}

	public Set<Long> getQueryIds() {
		return queryIds;
	}

	public void setQueryIds(Set<Long> queryIds) {
		this.queryIds = queryIds;
	}

	public Map<Long, Map<BaseTypeField, Object>> getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(Map<Long, Map<BaseTypeField, Object>> queryResult) {
		this.queryResult = queryResult;
	}

	public ColumnProcessor<BaseTypeField> getCp() {
		return cp;
	}

	public void setCp(ColumnProcessor<BaseTypeField> cp) {
		this.cp = cp;
	}

	public List<ConditionPair<ConditionField>> getNoSubConditions() {
		return noSubConditions;
	}

	public void setNoSubConditions(
			List<ConditionPair<ConditionField>> noSubConditions) {
		this.noSubConditions = noSubConditions;
	}

	public AbstractQueryFacade<?, BaseTypeField, ConditionField> getFacade() {
		return facade;
	}

	public void setFacade(
			AbstractQueryFacade<?, BaseTypeField, ConditionField> facade) {
		this.facade = facade;
	}

	public boolean isOneToMany() {
		return isOneToMany;
	}

	public void setOneToMany(boolean isOneToMany) {
		this.isOneToMany = isOneToMany;
	}

	public ModelTree getParent() {
		return parent;
	}

	public void setParent(ModelTree parent) {
		this.parent = parent;
	}

	public boolean isMapping() {
		return isMapping;
	}

	public void setMapping(boolean isMapping) {
		this.isMapping = isMapping;
	}

}
