/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.processor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haoyayi.thor.api.BaseTypeField;
import com.haoyayi.thor.api.ErrorCode;
import com.haoyayi.thor.api.ModelType;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.manager.ContextManager;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.exception.BizException;
import com.haoyayi.thor.impl.base.OpType;
import com.haoyayi.thor.utils.DateUtils;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractColumnProcessor<R extends BaseTypeField> implements ColumnProcessor<R>, InitializingBean, ApplicationContextAware {

    @Autowired
    protected ContextManager contextManager;

    @Autowired
    protected ProcessorContext processorContext;

    protected ModelContext modelContext;

    protected Map<String, String> subModelField4subModel;
    
    protected Map<String, String> subModel4subModelRefId;
    
    /**
     * ref mapping field -> main model field
     */
    private Map<String, String> field2mappingMainModel = Maps.newHashMap();
    
    /**
     * field -> sub model field
     */
    private Map<String, String> field2mappingSubModel = Maps.newHashMap();
    
    private Map<String, String> subModelIdField2refMappingField = Maps.newHashMap();
    
    private Map<String, String> subModelIdField2subModelField = Maps.newHashMap();
    
    private Map<String, String> subModelIdField2subModelType = Maps.newHashMap();
    
    private Map<String, String> subModelName2SubModelType = Maps.newHashMap();
    
    private Map<String, String> subModelType2SubModelName = Maps.newHashMap();
    
    protected Map<R, Set<R>> field4OtherFields = new HashMap<R, Set<R>>();
    
    protected ApplicationContext applicationContext;

    protected Map<String, R> field4enum = new HashMap<String, R>();

    protected Map<String, String> subModel4pk;
    
    protected Map<String, String> field2mappingSubModelPk;

    protected static List<String> NUMBER_TYPE = Lists.newArrayList("Integer", "Long", "Float", "Double");
    
    protected static List<String> ARRAY_TYPE = Lists.newArrayList("ArrayList");
    
    protected static String DATE_TYPE = "java.util.Date";

    protected abstract ModelType getModelType();

    public Boolean isDict() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public R getPkField() {
        return convert(modelContext.getPk());
    }

    /**
     * 各种初始化
     */
    private void init() {

        // 初始化modelcontext
        if (modelContext == null) {
            if (isDict()) {
                modelContext = contextManager.getDictContext(this.getModelType().name());
            } else {
                modelContext = contextManager.getContext(this.getModelType().name());
            }
        }

        // submodel相关
        subModel4pk = new HashMap<String, String>();
        subModelField4subModel = new HashMap<String, String>();
        subModel4subModelRefId = new HashMap<String, String>();
        field2mappingMainModel = Maps.newHashMap();
        field2mappingSubModel = Maps.newHashMap();
        field2mappingSubModelPk = Maps.newHashMap();
        for (FieldContext fieldContext : modelContext.getRefModelField()) {
            String subModel = fieldContext.getRefModel();
            subModelField4subModel.put(fieldContext.getName(), subModel);
            ModelContext subModelContext = processorContext.getConverter(subModel).getModelContext();
            subModel4pk.put(fieldContext.getName(), subModelContext.getPk());
            subModel4subModelRefId.put(fieldContext.getName(), fieldContext.getModelField4RefModelField().get(fieldContext.getRefModelPk()));
        }
        for (FieldContext fieldContext : modelContext.getRefMappingModelField()) {
        	field2mappingSubModel.put(fieldContext.getName(), fieldContext.getField2mappingSubModel().get(fieldContext.getName()));
        	field2mappingMainModel.put(fieldContext.getName(), fieldContext.getField2mappingMainModel().get(fieldContext.getName()));
        	ModelContext subModelContext = processorContext.getConverter(field2mappingSubModel.get(fieldContext.getName())).getModelContext();
        	field2mappingSubModelPk.put(fieldContext.getName(), subModelContext.getPk());
        	for (String subModelIdField : fieldContext.getSubModelField2refMappingField().keySet()) {
        		subModelIdField2refMappingField.put(subModelIdField, fieldContext.getSubModelField2refMappingField().get(subModelIdField));
        		subModelIdField2subModelField.put(subModelIdField, fieldContext.getSubModelIdField2subModelField().get(subModelIdField));
        		subModelIdField2subModelType.put(subModelIdField, fieldContext.getSubModelIdField2subModelType().get(subModelIdField));
        	}
        	for (String subModelField : fieldContext.getSubModelField2subModelType().keySet()) {
        		subModelName2SubModelType.put(subModelField, fieldContext.getSubModelField2subModelType().get(subModelField));
        		subModelType2SubModelName.put(fieldContext.getSubModelField2subModelType().get(subModelField), subModelField);
        	}
        }

        // field相关
        for (R field : getFields()) {
            field4enum.put(field.toString(), field);
        }
        
        for (R field : getFields()) {
            Set<R> otherFields = new HashSet<R>();
            FieldContext fieldContext = modelContext.getField4meta().get(field.toString());
            if (fieldContext == null) {
                continue;
            }
            Set<String> strOtherFields = fieldContext.getRelationFields();
            for (String strOtherField : strOtherFields) {
                otherFields.add(convert(strOtherField));
            }
            field4OtherFields.put(field, otherFields);
        }
    }

    /**
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * field转换成R
     *
     * @param field
     * @return
     */
    public R convert(String field) {
        if (field4enum.containsKey(field)) {
            return field4enum.get(field);
        } else {
            // field转化出问题。
            throw new BizException("The field \'" + field + "\' is invalid.");
        }
    }

    /**
     * 进行类型转换
     *
     * @param field
     * @param value
     * @param context
     * @return
     */
    public Optional<BizError> convertFieldValueType(R field, Object value, Map<R, Object> context) {
        FieldContext fieldContext = modelContext.getField4meta().get(field.toString());
        if (value == null ) {
            // 修改成允许为null.
            // return Optional.of(new BizError(ErrorCode.ERROR_PARAM_COMMON_ERROR, "The field: \'" + field + "\' is null.", field.toString()));
            return Optional.fromNullable(null);
        }
        String valueClass = value.getClass().getSimpleName();
        String javaType = fieldContext.getFieldType().getJavaType();

        if (valueClass.equals(javaType)) {
            context.put(field, value);
        } else if (NUMBER_TYPE.contains(valueClass)) {
            context.put(field, convertNumber(javaType, value));
        } else if (DATE_TYPE.equals(javaType) && "String".equals(valueClass)) {
        	context.put(field, DateUtils.parseDate((String) value));
        } else if (DATE_TYPE.equals(javaType) && "Date".equals(valueClass)) {
        	context.put(field, value);
        } else if (fieldContext.hasOption() && (fieldContext.getFieldOption().getNewtable() || fieldContext.getFieldOption().getRefModel()) || fieldContext.getFieldOption().getRefMappingModel()) {
        	context.put(field, value);
        } else if (ARRAY_TYPE.contains(valueClass)) {
        	context.put(field, convertArrays(javaType, value));
        } else {
            return Optional.of(new BizError(ErrorCode.ERROR_PARAM_COMMON_ERROR, "The field type \'" + valueClass + "\' is invalid. Maybe is " + javaType, field.toString()));
        }
        return Optional.fromNullable(null);
    }

    /**
     * 根据其java type进行转换
     *
     * @param javaType
     * @param value
     * @return
     */
    protected Object convertNumber(String javaType, Object value) {
        try {
            Class clazz = value.getClass();
            Method method = clazz.getDeclaredMethod(CamelUtils.lowerFirst(javaType) + "Value");
            return method.invoke(value);
        } catch (Exception e) {
            throw new BizException("Convert context type field error.", e);
        }
    }
    
    /**
     * 根绝javatype将集合类型的value进行类型转换
     * @param javaType
     * @param value
     * @return
     */
    protected Object convertArrays(String javaType, Object value) {
    	Object[] arrays = null;
    	String elementJavaType = javaType.substring(0, javaType.length() - 2);
    	try {
    		arrays = (Object[]) Array.newInstance(Class.forName("java.lang." + elementJavaType), (Integer) value.getClass().getDeclaredMethod("size").invoke(value));
			Method method = value.getClass().getDeclaredMethod("iterator");
			Iterator<?> iterator = (Iterator<?>) method.invoke(value);
			if (iterator != null) {
				int i = 0;
				while (iterator.hasNext()) {
					Object o = iterator.next();
					if (!o.getClass().getSimpleName().equalsIgnoreCase(elementJavaType)) {
						o = convertNumber(elementJavaType, o);
					}
					arrays[i++] = o;
				}
			}
		} catch (Exception e) {
			throw new BizException("Convert context type field error.", e);
		} 
    	return arrays;
    }

    /**
     * 构建子model
     *
     * @param model
     * @param subModels
     * @param context
     * @return
     */
    public Object buildSubModels(String model, Object subModels, Map<OpType, Map<Long, Map<R, Object>>> context) {

        Map<Long, Map<R, Object>> addContext = context.get(OpType.ADD);
        Map<Long, Map<R, Object>> modContext = context.get(OpType.MOD);

        if (addContext == null) {
            addContext = new LinkedHashMap<Long, Map<R, Object>>();
        }

        if (modContext == null) {
            modContext = new LinkedHashMap<Long, Map<R, Object>>();
        }

        long index = addContext.size();

        if (subModels.getClass().isArray() || subModels instanceof ArrayList) {

            // sub model为数组。
            List<Map<String, Object>> subModelArray = null;
            if (subModels.getClass().isArray()) {
                subModelArray = Arrays.asList((Map<String, Object>[]) subModels);

            } else {
                subModelArray = (ArrayList<Map<String, Object>>) subModels;
            }
            List<Map<R, Object>> result = new ArrayList<Map<R, Object>>();

            for (Map<String, Object> strSubModel : subModelArray) {
                Long subModelPk = null;
                Map<R, Object> subModel = new LinkedHashMap<R, Object>();
                for (String field : strSubModel.keySet()) {
                    subModel.put((R)processorContext.getConverter(model).convert(field), strSubModel.get(field));
                }
                if (strSubModel.get(subModel4pk.get(model)) != null) {
                    subModelPk = ((Number) strSubModel.get(subModel4pk.get(model))).longValue();
                }

                if (subModelPk == null || subModelPk == 0L) {
                    addContext.put(index++, subModel);
                } else {
                    modContext.put(subModelPk, subModel);
                }

                result.add(subModel);
            }

            context.put(OpType.ADD, addContext);
            context.put(OpType.MOD, modContext);

            return result.toArray(new Map[0]);

        } else {

            Map<String, Object> strSubModel = (Map<String, Object>) subModels;
            Map<R, Object> subModel = new LinkedHashMap<R, Object>();
            Long subModelPk = 0L;
            for (String field : strSubModel.keySet()) {
                subModel.put((R)processorContext.getConverter(model).convert(field), strSubModel.get(field));
            }

            if (strSubModel.get(subModel4pk.get(model)) != null) {
                subModelPk = ((Number) strSubModel.get(subModel4pk.get(model))).longValue();
            }

            if (subModelPk == null || subModelPk == 0L) {
                addContext.put(index++, subModel);
            } else {
                modContext.put(subModelPk, subModel);
            }

            context.put(OpType.ADD, addContext);
            context.put(OpType.MOD, modContext);

            return subModel;
        }

    }

    /**
     * @param context
     * @return
     */
    public Map<String, Map<OpType, Map<Long, Map<R, Object>>>> getSubModels(Map<Long, Map<R, Object>> context, OpType action) {
        Map<String, Map<OpType, Map<Long, Map<R, Object>>>> result = new LinkedHashMap<String, Map<OpType, Map<Long, Map<R, Object>>>>();
        for (Long id : context.keySet()) {
            Map<R, Object> contextField = context.get(id);
            for (R field : contextField.keySet()) {
                Object value = contextField.get(field);
                if (this.subModelField4subModel.containsKey(field.toString())) {
                    String model = subModelField4subModel.get(field.toString());
                    Map<OpType, Map<Long, Map<R, Object>>> id2submodel = result.get(model);
                    if (id2submodel == null) {
                        id2submodel = new LinkedHashMap<OpType, Map<Long, Map<R, Object>>>();
                    }
                    buildSubModels(id, model, value, id2submodel, action);
                    result.put(model, id2submodel);
                }
            }
        }
        return result;
    }
    
    public Map<String, Map<OpType, Map<Long, Map<R, Object>>>> getRefMappingModels(Map<Long, Map<R, Object>> context, OpType action) {
    	Map<String, Map<OpType, Map<Long, Map<R, Object>>>> result = new LinkedHashMap<String, Map<OpType, Map<Long, Map<R, Object>>>>();
        for (Long id : context.keySet()) {
            Map<R, Object> contextField = context.get(id);
            for (R field : contextField.keySet()) {
                if (field2mappingSubModel.containsKey(field.toString())) {
                	String subModel = field2mappingSubModel.get(field.toString());
                	result.put(field.toString(), buildMappingSubModels(id, field.toString(), subModel, contextField.get(field)));
                }
            }
        }
        return result;
    }
   
    /**
     * 从主model中构建出多对多关系的子model
     * @param subModelField 主model中引用的子model的field
     * @param subModelName 子model实际的名字
     */
    public Map<OpType, Map<Long, Map<R, Object>>> buildMappingSubModels(Long mainModelPk, String subModelField, String subModelName, Object value) {
    	Map<OpType, Map<Long, Map<R, Object>>> result = Maps.newHashMap();
    	Map<Long, Map<R, Object>> addContexts = Maps.newHashMap();
    	Map<Long, Map<R, Object>> modContexts = Maps.newHashMap();
    	String subModelPk = field2mappingSubModelPk.get(subModelField);
    	ColumnProcessor<R> subColumnProcessor = processorContext.getConverter(subModelName);
    	Long addIndex = 0l;
    	Long modIndex = null;
    	Collection<Map<String, Object>> subModelArray = null;
    	if (value instanceof Collection<?>) {
    		subModelArray = (Collection<Map<String, Object>>) value;
    	} else if (value instanceof Object[]) {
    		subModelArray = Arrays.asList((Map<String, Object>[]) value);
    	}
    	List<Map<R, Object>> subModelContexts = Lists.newArrayList();
    	for (Map<String, Object> subModelContext : subModelArray) {
    		Map<R, Object> context = Maps.newHashMap();
    		for (Object field : subModelContext.keySet()) {
    			if (field instanceof String) {
    				context.put(subColumnProcessor.convert((String) field), subModelContext.get(field));
    			} else if (field instanceof BaseTypeField) {
    				context.put((R) field, subModelContext.get(field));
    			} else if (field instanceof Number) {
    				context.putAll((Map) subModelContext.get(field));
    			}
    		}
    		subModelContexts.add(context);
    	}
    	for (Map<R, Object> subModelContext : subModelContexts) {
    		boolean findPk = false;
    		for (R field : subModelContext.keySet()) {
                if (field.toString().equals(subModelPk)) {
                	findPk = true;
                	if (subModelContext.get(field) == null || ((Number) subModelContext.get(field)).longValue() == 0) {
                        addContexts.put(addIndex++, subModelContext);
                    } else {
                        modIndex = ((Number) subModelContext.get(field)).longValue();
                        modContexts.put(modIndex, subModelContext);
                    }
                	break;
                }
    		}
    		if (!findPk) {
    			addContexts.put(addIndex++, subModelContext);
    		}
    		if (mainModelPk != null) {
    			subModelContext.put((R)processorContext.getConverter(subModelName).getRefModelPk(), mainModelPk);
    		}
    	}
    	if (MapUtils.isNotEmpty(addContexts)) {
    		result.put(OpType.ADD, addContexts);
    	}
    	if (MapUtils.isNotEmpty(modContexts)) {
    		result.put(OpType.MOD, modContexts);
    	}
    	return result;
    }

    /**
     * 主model中build出子model
     *
     * @param mainModelPk
     * @param model
     * @param subModels
     * @param context
     */
    public void buildSubModels(Long mainModelPk, String model, Object subModels, Map<OpType, Map<Long, Map<R, Object>>> context, OpType action) {
        // 设置为null，则直接返回。
        if (subModels == null) {
            return;
        }
        String subField = getSubModelFieldFromSubModel(model);
        String pk = subModel4pk.get(subField);

        Map<Long, Map<R, Object>> addContext = context.get(OpType.ADD);
        Map<Long, Map<R, Object>> modContext = context.get(OpType.MOD);
        if (addContext == null) {
            addContext = new LinkedHashMap<Long, Map<R, Object>>();
        }
        if (modContext == null) {
            modContext = new LinkedHashMap<Long, Map<R, Object>>();
        }
        long index = addContext.size();

        if (subModels.getClass().isArray() || subModels instanceof ArrayList) {
        	 	
            // sub model为数组。
            List<Map<R, Object>> subModelArray = null;
            if (subModels.getClass().isArray()) {
                subModelArray = Arrays.asList((Map<R, Object>[]) subModels);

            } else {
                subModelArray = (ArrayList<Map<R, Object>>) subModels;
            }

            for (Map<R, Object> subModel : subModelArray) {
                boolean add = false;
                boolean hasPk = false;
                Long subModelPk = 0L;
                for (R field : subModel.keySet()) {
                    hasPk = true;
                    if (field.toString().equals(pk)) {
                        if (subModel.get(field) == null || ((Number) subModel.get(field)).longValue() == 0) {
                            add = true;
                        } else {
                            add = false;
                            subModelPk = ((Number) subModel.get(field)).longValue();
                        }
                        break;
                    } else {
                        add = true;
                    }
                }
                if (hasPk && !add) {
                    if (action==OpType.ADD) {
                        addContext.put(index++, subModel);
                    } else {
                        modContext.put(subModelPk, subModel);
                    }
                    subModel.put((R)processorContext.getConverter(model).getRefModelPk(), mainModelPk);
                } else {
                    // 设置子model关联的主model pk  只针对add操作
                    subModel.put((R)processorContext.getConverter(model).getRefModelPk(), mainModelPk);
                    addContext.put(index++, subModel);
                }
            }
        } else {
            Map<R, Object> subModel = (Map<R, Object>) subModels;
            boolean add = false;
            boolean hasPk = false;
            Long subModelPk = 0L;
            for (R field : subModel.keySet()) {

                if (field.toString().equals(pk)) {
                    hasPk = true;
                    if (subModel.get(field) == null || ((Number) subModel.get(field)).longValue() == 0) {
                        add = true;
                    } else {
                        add = false;
                        subModelPk = ((Number) subModel.get(field)).longValue();
                    }
                }
            }
            if (hasPk && !add) {
                if (action==OpType.ADD) {
                    addContext.put(index++, subModel);
                } else {
                    modContext.put(subModelPk, subModel);
                }
                subModel.put((R)processorContext.getConverter(model).getRefModelPk(), mainModelPk);
            } else {
                // 设置子model关联的主model pk  只针对add操作
                subModel.put((R)processorContext.getConverter(model).getRefModelPk(), mainModelPk);
                addContext.put(index++, subModel);
            }
        }
        context.put(OpType.ADD, addContext);
        context.put(OpType.MOD, modContext);
    }


    /**
     * 获得Context
     *
     * @return
     */
    @Override
    public ModelContext getModelContext() {
        return modelContext;
    }

    /**
     * 判断是否包括submodel
     *
     * @return
     */
    public Boolean containsSubModel() {
        return subModelField4subModel.size() > 0;
    }
    
    public Boolean containsRefMappingModel() {
    	return field2mappingMainModel.size() > 0;
    }

    /**
     * 是否是子model field
     *
     * @param field
     * @return
     */
    public Boolean isSubModelField(String field) {
        return subModelField4subModel.containsKey(field);
    }


    public String getSubModelFieldFromSubModel(String field) {
        for (String subField : subModelField4subModel.keySet()) {
            if (subModelField4subModel.get(subField).equals(field)) {
                return subField;
            }
        }
        return null;
    }

    public Boolean isMultiSubModelField(String field) {
        String subField = getSubModelFieldFromSubModel(field);
        if (subField!=null) {
            if(modelContext.getField4meta().get(subField).getFieldType().getMetaType().equals("obj[]")){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public Boolean isAnytimeNewField(String field) {
    	String subField = getSubModelFieldFromSubModel(field);
        if (subField!=null) {
        	return modelContext.getField4meta().get(subField).getFieldOption().getAnytimeNew();
        } else {
            return false;
        }
    }
    
    public Boolean isAnytimeNewMappingField(String field) {
    	FieldContext fieldContext = getModelContext().getFieldContext(field);
    	if (fieldContext != null) {
    		return fieldContext.getFieldOption().getAnytimeNew();
    	}
    	return false;
    }
    
    @Override
	public Boolean isMappingSubModelField(String field) {
		FieldContext fieldContext = modelContext.getFieldContext(field);
		return fieldContext != null && StringUtils.isNotEmpty(fieldContext.getRefMappingModel());
	}
    
    public Map<String, String> getSubModelIdField2refMappingField() {
    	return subModelIdField2refMappingField;
    }
    
	@Override
	public Map<String, String> getSubModelIdField2subModelField() {
		return subModelIdField2subModelField;
	}

	@Override
	public String getMapptingSubModelName(String field) {
		return field2mappingSubModel.get(field);
	}

	/**
     * 根据field获得子modelfield
     *
     * @param field
     * @return
     */
    public String getSubModel(String field) {
        return subModelField4subModel.get(field);
    }
    
    @Override
	public String getSubModelTypeFromSubModelName(String field) {
		return subModelName2SubModelType.get(field);
	}

	@Override
	public String getSubModelNameFromSubModelType(String field) {
		return subModelType2SubModelName.get(field);
	}

	/**
     * 获得子model pk
     *
     * @param subModel
     * @return
     */
    public String getSubModelPk(String subModel) {
        return subModel4pk.get(subModel);
    }
    
    /**
     * 获得子model在父model的引用id
     * @param subModel
     * @return
     */
    public String getSubModelRefId(String subModel) {
    	return subModel4subModelRefId.get(subModel);
    }
    
    public Set<R> getOtherNecessaryFields(R field) {
    	return field4OtherFields.get(field);
    }

	public Map<String, String> getSubModelIdField2subModelType() {
		return subModelIdField2subModelType;
	}
    
}
