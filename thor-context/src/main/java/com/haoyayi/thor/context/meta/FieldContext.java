/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.context.meta;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.manager.ContextManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class FieldContext {

    public static final String TYPE_SEP = "$";
    public static final String TYPE_SEP_PREFIX = "{";
    public static final String TYPE_SEP_SUFFIX = "}";

    public static final String OPTION_PREFIX = "@";
    public static final String TYPE_NO_STORE = "@nostore";
    public static final String TYPE_READ_ONLY = "@readonly";

    public static final String TYPE_NO_EXPOSE = "@noexpose";

    public static final String TYPE_NEW_TALBE = "@newtable";

    public static final String TYPE_NEW_DATE = "@newdate";

    public static final String TYPE_UPDATE_DATE = "@updatedate";

    public static final String TYPE_AUTO_KEY = "@autokey";

    public static final String TYPE_REF_MODEL = "@refmodel";

    public static final String TYPE_REF_MODELS = "@refsmodel";
    
    public static final String TYPE_REF_MAPPING_MODEL = "@refsmappingmodel";

    public static final String TYPE_MAPPING_MODEL = "@mappingmodel";

    public static final String TYPE_REF_DICT = "@refdict";

    public static final String TYPE_DEL = "@del";

    public static final String TYPE_DICT = "@dict";

    public static final String TYPE_OBJ_REPL = "${obj}";

    public static final String TYPE_ID = "@id";

    public static final String TYPE_DICT_JOINER = "@joiner";

    public static final String TYPE_DICT_MASK = "@mask";

    public static final String TYPE_ARRAY = "[]";
    
    public static final String DENTIST_SIGN = "@dentistsign";
    
    public static final String TYPE_ANYTIME_NEW = "@anytimenew";

    private Boolean exclude = false;


    /****************Start Meta Field****************/

    /**
     * meta: ${field_name}, ${field_type}, ${field_store}(可选), ${field_other}(@x+@y)
     * - meta: pinyin, str${255}, wmkq.dentist_add_info1, @noexpose
     */
    private String meta;
    /**
     * 基本描述信息
     */
    private String desc;
    /**
     * context信息，composite一个新的ModelContext.
     */
    private ModelContext context;

    /****************End Meta Field****************/

    /**
     * 其Parent ModelContext
     */
    private ModelContext parentContext;

    /**
     * 字段名称。
     */
    private String name;
    /**
     * 字段类型信息
     */
    private FieldType fieldType;
    /**
     * 字段存储信息
     */
    private FieldStore fieldStore;
    /**
     * 字段选项信息
     */
    private FieldOption fieldOption;

    private String refModel;
    
    private String refModelPk;

    private String modelRef;

    private String relFields;
    
    private String refMappingModel;

    /**
     * field关联查询字段
     */
    private Set<String> relationFields = new HashSet<String>();

    /**
     * ref model field -> main model field
     */
    private Map<String, String> refModelField4ModelField = new HashMap<String, String>();
    
    /**
     * ref mapping field -> main model field
     */
    private Map<String, String> refMappingField2MainModelField = Maps.newHashMap();
    
    private Map<String, String> field2mappingMainModel = Maps.newHashMap();
    
    private Map<String, String> field2mappingSubModel = Maps.newHashMap();  
    
    /**
     * ref mapping field -> sub model field
     */
    private Map<String, String> refMappingField2SubModelField = Maps.newHashMap();
    
    private Map<String, String> subModelField2refMappingField = Maps.newHashMap();
    
    private Map<String, String> subModelIdField2subModelType = Maps.newHashMap();
    
    private Map<String, String> subModelIdField2subModelField = Maps.newHashMap();
    
    private Map<String, String> subModelField2subModelType = Maps.newHashMap();
    
    private Map<String, String> subModelType2subModelField = Maps.newHashMap();

    /**
     * main model field -> ref model field
     */
    private Map<String, String> modelField4RefModelField = new HashMap<String, String>();

    private String dictDao = "";

    private Map<String, String> refDict2Dictid = new HashMap<String, String>();

    private Map<String, String> dictId2RefDict = new HashMap<String, String>();

    private String refExposeField = "";
    
    public boolean isStore() {
        return this.fieldStore != null;
    }

    public boolean hasOption() {
        return this.fieldOption != null;
    }

    private FieldContext buildRefDict(ApplicationContext context, FieldContext touchedField, ModelContext parentContext, String model, String meta, String desc) {

        FieldContext fieldContext = new FieldContext();
        fieldContext.setParentContext(parentContext);
        fieldContext.setMeta(meta);
        if (fieldContext.getFieldOption() == null) {
            fieldContext.setFieldOption(new FieldOption());
        }
        fieldContext.getFieldOption().setRefDict(true);
        fieldContext.refresh(context);
        fieldContext.getParentContext().getRefDictField().add(fieldContext);
        fieldContext.getParentContext().getRefDict4Field().put(fieldContext.getName(), fieldContext);

        fieldContext.setDesc(desc + "对应的字典值字段");

        ContextManager contextManager = context.getBean("contextManager", ContextManager.class);

        ModelContext dictContext = contextManager.getDictContext(model);

        fieldContext.dictDao = CamelUtils.getCamelStr(dictContext.getPkField().getFieldStore().getTable())+"DAOImpl";
        fieldContext.refDict2Dictid.put(fieldContext.getName(), touchedField.getName());
        fieldContext.dictId2RefDict.put(touchedField.getName(), fieldContext.getName());
        fieldContext.relationFields.add(touchedField.getName());

        if (fieldContext.hasOption() && (fieldContext.getFieldOption().getDictJoiner() || fieldContext.getFieldOption().getDictMask())) {
            fieldContext.refreshFieldType(context, "str[]");
        }

        return fieldContext;

    }

    private FieldContext buildRefModel(ApplicationContext context, FieldContext touchedField, ModelContext parentContext, String model, String meta, String desc, String modelKey, String mappingModelKey) {

        boolean mapping = StringUtils.isNotBlank(modelKey) && StringUtils.isNotBlank(mappingModelKey);

        FieldContext fieldContext = new FieldContext();
        fieldContext.setRefModel(model);
        fieldContext.setParentContext(parentContext);
        fieldContext.setMeta(meta);
        if (fieldContext.getFieldOption() == null) {
            fieldContext.setFieldOption(new FieldOption());
        }
        fieldContext.getFieldOption().setRefModel(true);
        fieldContext.refresh(context);
        fieldContext.getParentContext().getRefModelField().add(fieldContext);
        fieldContext.getParentContext().getRefModel4Field().put(fieldContext.getName(), fieldContext);

        // refmodel loader

        ContextManager contextManager = context.getBean("contextManager", ContextManager.class);

        if (mapping) {
        	StringBuilder descSb = new StringBuilder();
        	descSb.append(desc);
        	descSb.append("对应的");
        	descSb.append(model);
        	descSb.append("\n" +
        			"\t *").append(" 这是个引用字段，可以通过").append(fieldContext.getName()).append("_").append("{@linkplain com.haoyayi.thor.api." + fieldContext.getRefModel() + ".dto." + CamelUtils.upperFirst(fieldContext.getRefModel()) + "TypeField }").append("来进行查询。");
        	
        	fieldContext.setDesc(descSb.toString());
        	fieldContext.setRefModelPk("id");
        	fieldContext.relationFields.add(modelKey);
        	fieldContext.refModelField4ModelField.put(modelKey, mappingModelKey);
        	fieldContext.modelField4RefModelField.put(mappingModelKey, modelKey);
        } else {

            ModelContext refModelContext = contextManager.getContext(fieldContext.getRefModel(), false);
            StringBuilder descSb = new StringBuilder();
            descSb.append(desc);
            descSb.append("对应的");
            descSb.append(refModelContext.getDesc());
            descSb.append("\n" +
                    "\t *").append(" 这是个引用字段，可以通过").append(fieldContext.getName()).append("_").append("{@linkplain com.haoyayi.thor.api." + fieldContext.getRefModel() + ".dto." + CamelUtils.upperFirst(fieldContext.getRefModel()) + "TypeField }").append("来进行查询。");

            fieldContext.setDesc(descSb.toString());

            fieldContext.setRefModelPk(refModelContext.getPk());
            fieldContext.relationFields.add(touchedField.getName());
            fieldContext.refModelField4ModelField.put(touchedField.getName(), refModelContext.getPk());
            fieldContext.modelField4RefModelField.put(refModelContext.getPk(), touchedField.getName());
        }

        return fieldContext;

    }

    public void refresh(ApplicationContext context) {
        List<String> fields = Splitter.on(",").trimResults().splitToList(meta);

        this.name = fields.get(0);
        String metaType = fields.get(1);
        String store = fields.get(2);
        String option = null;
        if (store.startsWith(OPTION_PREFIX)) {
            store = null;
            option = fields.get(2);
        }

        if (fields.size() == 4) {
            option = fields.get(3);
        }
        refreshFieldOption(context, option);
        refreshFieldStore(context, store);
        refreshFieldType(context, metaType);
        /*
        if (modelRef!=null) {
            processModelRef();
        }
        */

        refreshRelFields();
    }

    /*
    private void processModelRef() {
        List<String> fields = Splitter.on(",").trimResults().splitToList(modelRef);
        for (String field : fields) {
            List<String> refFields = Splitter.on(MODELREF_TOKEN).trimResults().splitToList(field);
            refModelField4ModelField.put(refFields.get(0),refFields.get(1));
            modelField4RefModelField.put(refFields.get(1),refFields.get(0));
        }
    }
    */

    private void refreshRelFields() {
        relationFields = new HashSet<String>();
        if (relFields != null) {
            List<String> fields = Splitter.on(",").trimResults().splitToList(relFields);
            relationFields.addAll(fields);
        }
    }


    private void refreshFieldType(ApplicationContext context, String metaType) {

        if (StringUtils.isBlank(metaType)) {
            return;
        }

        this.fieldType = new FieldType();

        CodegenContext codegenContext = context.getBean("codegenContext", CodegenContext.class);

        String rawType, javaType, mysqlType = null;

        Integer rawTypeLen = 0;

        if (metaType.contains(TYPE_ARRAY)) {
            if (!hasOption()) {
                this.fieldOption = new FieldOption();
            }
            this.fieldOption.setArray(true);
        }

        if (metaType.startsWith(TYPE_ID)) {
            this.getParentContext().setRefPkField(this);
            // 获得Root Model的 pk 类型。
            if (metaType.equals(TYPE_ID)) {
                metaType = getParentContext().getRoot().getPkField().getFieldType().getMetaType();
            } else {
                String field = metaType.substring(metaType.indexOf("+") + 1);
                metaType = getParentContext().getRoot().getField4meta().get(field).getFieldType().getMetaType();
            }
        }

        if (metaType.contains(TYPE_SEP)) {
            rawType = metaType.substring(0, metaType.indexOf(TYPE_SEP));
            rawTypeLen = Integer.parseInt(metaType.substring(metaType.indexOf(TYPE_SEP_PREFIX) + 1, metaType.indexOf(TYPE_SEP_SUFFIX)));
        } else {
            rawType = metaType;
            if (isStore()) {
                rawTypeLen = codegenContext.getTypedblen().get(rawType);
                if (rawTypeLen == null) {
                	rawTypeLen = 0;
                }
            }
        }

        if (hasOption() && this.fieldOption.getNewtable()) {
            javaType = getNewTableJavaType(codegenContext.getTypemap().get(rawType));
            this.getParentContext().getNewTableField().add(this);
        } else if (hasOption() && this.fieldOption.getRefModel()) {
            javaType = getRefModelJavaType(codegenContext.getTypemap().get(rawType));
        } else {
            javaType = codegenContext.getTypemap().get(rawType);
        }
        if (isStore()) {
            mysqlType = codegenContext.getTypedb().get(rawType);
        }

        this.fieldType.setJavaType(javaType);
        this.fieldType.setMetaType(metaType);
        this.fieldType.setMysqlType(mysqlType);
        this.fieldType.setRawType(rawType);
        this.fieldType.setRawTypeLen(rawTypeLen);

        if (this.name.equals(this.getParentContext().getPk())) {
            this.getParentContext().setPkField(this);
        }

    }

    private void refreshFieldStore(ApplicationContext context, String store) {

        if (StringUtils.isBlank(store)) {
            return;
        }
        this.fieldStore = new FieldStore();
        List<String> fields = Splitter.on(".").trimResults().splitToList(store);
        this.fieldStore.setDb(fields.get(0));
        this.fieldStore.setTable(fields.get(1));

    }

    private void refreshFieldOption(ApplicationContext context, String options) {

        if (fieldOption == null) {
            fieldOption = new FieldOption();
        }

        if (fieldType != null && fieldType.getJavaType().equals("Boolean")) {
            fieldOption.setBoolType(true);
        }

        if (StringUtils.isBlank(options)) {
            return;
        }

        List<String> fields = Splitter.on("+").trimResults().splitToList(options);
        for (String field : fields) {
            if (field.equals(TYPE_NO_EXPOSE)) {
                fieldOption.setExpose(false);
            }
            if (field.equals(TYPE_NO_STORE)) {
                fieldOption.setStore(false);
            }
            if (field.equals(TYPE_READ_ONLY)) {
                fieldOption.setReadonly(true);
            }
            if (field.equals(TYPE_DICT_JOINER)) {
                fieldOption.setDictJoiner(true);
            }
            if (field.equals(TYPE_DICT_MASK)) {
                fieldOption.setDictMask(true);
            }
            if (field.equals(TYPE_NEW_TALBE)) {
                fieldOption.setNewtable(true);
                if (this.context != null) {
                    this.context.setParent(this.getParentContext());
                    this.context.refresh(context);
                    this.context.setChild(true);
                }
            }
            if (field.equals(TYPE_ANYTIME_NEW)) {
            	fieldOption.setAnytimeNew(true);
            }

            if (field.startsWith(TYPE_REF_MODEL)) {
                if (field.contains("(")) {
                    String plus = field.substring(field.indexOf("(") + 1, field.length() - 1);
                    String model = "";
                    String name = "";
                    String type = "obj";
                    String refOptions = "@nostore";
                    if (plus.contains("-")) {
                        List<String> tokens = Splitter.on("-").trimResults().splitToList(plus);
                        name = tokens.get(0);
                        model = tokens.get(0);
                        for (int i = 1; i < tokens.size(); i++) {
                            String token = tokens.get(i);
                            if (token.startsWith(OPTION_PREFIX)) {
                                refOptions = refOptions + "+" + tokens.get(i);
                            } else {
                                // alias
                                name = token;
                            }
                        }
                    } else {
                        model = plus;
                        name = plus;
                    }
                    FieldContext refFieldContext = buildRefModel(context, this, this.getParentContext(), model, Joiner.on(",").join(new String[]{name, type, refOptions}), this.desc, null, null);
                    this.getParentContext().getField4meta().put(refFieldContext.getName(), refFieldContext);
                    this.getParentContext().getField().add(refFieldContext);
                }
            }

            if (field.startsWith(TYPE_REF_MODELS)) {
                if (field.contains("(")) {
                    String plus = field.substring(field.indexOf("(") + 1, field.length() - 1);
                    String model = "";
                    String refField = "";
                    if (plus.contains("-")) {
                        List<String> tokens = Splitter.on("-").trimResults().splitToList(plus);
                        model = tokens.get(0);
                        refField = tokens.get(1);
                    }
                    this.setRefModel(model);
                    if (this.getFieldOption() == null) {
                        this.setFieldOption(new FieldOption());
                    }
                    this.getFieldOption().setRefModel(true);
                    ContextManager contextManager = context.getBean("contextManager", ContextManager.class);
                    ModelContext refModelContext = contextManager.getContext(this.getRefModel(), false);
                    StringBuilder descSb = new StringBuilder();
                    descSb.append(desc);
                    descSb.append("对应的");
                    descSb.append(refModelContext.getDesc());
                    descSb.append("\n" +
                            "\t *").append(" 这是个引用字段，可以通过").append(this.getName()).append("_").append("{@linkplain com.haoyayi.thor.api." + this.getRefModel() + ".dto." + CamelUtils.upperFirst(this.getRefModel()) + "TypeField }").append("来进行查询。");

                    this.setDesc(descSb.toString());

                    this.setRefModelPk(refModelContext.getPk());
                    this.relationFields.add(getParentContext().getPk());
                    this.refModelField4ModelField.put(refField, getParentContext().getPk());
                    this.modelField4RefModelField.put(getParentContext().getPk(), refField);
                    this.getParentContext().getRefModelField().add(this);
                    this.getParentContext().getRefModel4Field().put(this.getName(), this);

                }
            }
            
            if (field.startsWith(TYPE_REF_MAPPING_MODEL)) {
            	if (field.contains("(")) {
            		String plus = field.substring(field.indexOf("(") + 1, field.length() - 1);
            		if (plus.contains("-")) {
            			List<String> elements = Splitter.on("-").trimResults().splitToList(plus);
            			String mappingModelName = elements.get(0);
            			String subModel = elements.get(1);
            			String mainModelField = elements.get(2);
            			String subModelFieldContext = elements.get(3);
            			String subModelField = "";
            			String subModelFieldAlias = "";
            			if (subModelFieldContext.contains("(")) {
            				subModelField = subModelFieldContext.substring(0, subModelFieldContext.indexOf("("));
            				subModelFieldAlias = subModelFieldContext.substring(subModelFieldContext.indexOf("(") + 1, subModelFieldContext.length() -1);
            			} else {
            				subModelField = subModelFieldContext;
            				subModelFieldAlias = subModelField;
            			}
            			refMappingModel = mappingModelName;
            			if (fieldOption == null) {
            				fieldOption = new FieldOption();
                        }
            			fieldOption.setRefMappingModel(true);
            			fieldOption.setStore(false);
            			refMappingField2MainModelField.put(mappingModelName, mainModelField);
            			refMappingField2SubModelField.put(mappingModelName, subModelField);
            			field2mappingMainModel.put(name, getParentContext().getName());
            			field2mappingSubModel.put(name, subModel);
            			subModelField2refMappingField.put(subModelFieldAlias, mappingModelName);
            			subModelIdField2subModelType.put(subModelFieldAlias, subModel);
            			subModelIdField2subModelField.put(subModelFieldAlias, name);
            			subModelField2subModelType.put(name, subModel);
            			subModelType2subModelField.put(subModel, name);
            			this.getParentContext().getField4meta().put(name, this);
            			StringBuilder descSb = new StringBuilder();
            			descSb.append(desc);
            			descSb.append("对应的");
            			descSb.append(subModelField);
            			descSb.append("\n" +
            					"\t *").append(" 这是个引用字段，可以通过").append(this.getName()).append("_").append("{@linkplain com.haoyayi.thor.api." + subModelField + ".dto." + CamelUtils.upperFirst(subModelField) + "TypeField }").append("来进行查询。");
            			this.setDesc(descSb.toString());
            			this.getParentContext().getRefMappingModelField().add(this);
            			this.getParentContext().getField4meta().put(name, this);
                        this.getParentContext().getField().add(this);
                        
                        FieldContext subModelIdField = new FieldContext();
                        subModelIdField.setParentContext(parentContext);
                        subModelIdField.setName(subModelFieldAlias);
                        subModelIdField.refreshFieldOption(context, "@nostore");
                        subModelIdField.refreshFieldType(context, "long[]");
                        this.getParentContext().getField4meta().put(subModelFieldAlias, subModelIdField);
            		}
            	}
            }

            if (field.startsWith(TYPE_MAPPING_MODEL)) {
                if (field.contains("(")) {
                    String plus = field.substring(field.indexOf("(") + 1, field.length() - 1);
                    String model = "";
                    String type = "obj[]";
                    String modelKey = "", mappingModelKey = "";
                    String refOptions = "@nostore";
                    if (plus.contains("-")) {
                        List<String> tokens = Splitter.on("-").trimResults().splitToList(plus);
                        model = tokens.get(0);
                        for (int i = 1; i < tokens.size(); i++) {
                            String token = tokens.get(i);
                            if (token.startsWith(OPTION_PREFIX)) {
                                refOptions = refOptions + "+" + tokens.get(i);
                            } else {
                                if (i==1) {
                                    modelKey = token;
                                } else if(i==2) {
                                    mappingModelKey = token;
                                }
                            }
                        }
                    } else {
                        model = plus;
                    }
                    FieldContext refFieldContext = buildRefModel(context, this, this.getParentContext(), model, Joiner.on(",").join(new String[]{model, type, refOptions}), this.desc, modelKey, mappingModelKey);
                    this.getParentContext().getField4meta().put(refFieldContext.getName(), refFieldContext);
                    this.getParentContext().getField().add(refFieldContext);
                    refFieldContext.exclude = false;
                    this.exclude = true;
                }
            }

            if (field.startsWith(TYPE_REF_DICT)) {
                if (field.contains("(")) {
                    String plus = field.substring(field.indexOf("(") + 1, field.length() - 1);
                    String model = "";
                    String name = "";
                    String refExposeId = "";
                    String type = "str";
                    String refOptions = "@nostore";

                    if (plus.contains("-")) {
                        List<String> tokens = Splitter.on("-").trimResults().splitToList(plus);
                        name = tokens.get(0);
                        model = tokens.get(0);
                        for (int i = 1; i < tokens.size(); i++) {
                            String token = tokens.get(i);
                            if (token.startsWith(OPTION_PREFIX)) {
                                refOptions = refOptions + "+" + tokens.get(i);
                            } else {
                                if (i==1) {
                                    // alias
                                    name = token;
                                } else if(i==2) {
                                    refExposeId = token;
                                }
                            }
                        }
                    } else {
                        model = plus;
                        name = plus;
                    }

                    FieldContext refFieldContext = buildRefDict(context, this, this.getParentContext(), model, Joiner.on(",").join(new String[]{name, type, refOptions}), this.desc);
                    refFieldContext.refExposeField = refExposeId;

                    this.getParentContext().getField4meta().put(refFieldContext.getName(), refFieldContext);
                    this.getParentContext().getField().add(refFieldContext);
                }
            }

            if (field.equals(TYPE_NEW_DATE)) {
                fieldOption.setNewdate(true);
            }

            if (field.equals(TYPE_UPDATE_DATE)) {
                fieldOption.setUpdateDate(true);
            }

            if (field.equals(TYPE_AUTO_KEY)) {
                fieldOption.setAutoKey(true);
            }

            if (field.equals(TYPE_DEL)) {
                fieldOption.setDel(true);
            }

            if (field.equals(TYPE_DICT)) {
                fieldOption.setDict(true);
            }
            if (field.equals(DENTIST_SIGN)) {
            	fieldOption.setDentistSign(true);
            }
        }

        if (!fieldOption.getExpose()) {
            setDesc(getDesc() +"，注意：该字段不会对外暴露，请不要请求该字段的信息！    @exclude");
        }
    }

    private String getNewTableJavaType(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(CamelUtils.upperFirst(this.name));
        sb.append("Type");

        return field.replace(TYPE_OBJ_REPL, sb.toString());
    }

    private String getRefModelJavaType(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("Map<");
        sb.append(CamelUtils.upperFirst(this.refModel));
        sb.append("TypeField, Object>");

        return field.replace(TYPE_OBJ_REPL, sb.toString());
    }

    public ModelContext getContext() {
        return context;
    }

    public void setContext(ModelContext context) {
        this.context = context;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelContext getParentContext() {
        return parentContext;
    }

    public void setParentContext(ModelContext parentContext) {
        this.parentContext = parentContext;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public FieldStore getFieldStore() {
        return fieldStore;
    }

    public void setFieldStore(FieldStore fieldStore) {
        this.fieldStore = fieldStore;
    }

    public FieldOption getFieldOption() {
        return fieldOption;
    }

    public void setFieldOption(FieldOption fieldOption) {
        this.fieldOption = fieldOption;
    }

    public String getRefModel() {
        return refModel;
    }

    public void setRefModel(String refModel) {
        this.refModel = refModel;
    }

    public String getRefModelPk() {
        return refModelPk;
    }

    public void setRefModelPk(String refModelPk) {
        this.refModelPk = refModelPk;
    }

    public String getModelRef() {
        return modelRef;
    }

    public void setModelRef(String modelRef) {
        this.modelRef = modelRef;
    }

    public Map<String, String> getRefModelField4ModelField() {
        return refModelField4ModelField;
    }

    public void setRefModelField4ModelField(Map<String, String> refModelField4ModelField) {
        this.refModelField4ModelField = refModelField4ModelField;
    }

    public String getRelFields() {
        return relFields;
    }

    public void setRelFields(String relFields) {
        this.relFields = relFields;
    }

    public Set<String> getRelationFields() {
        return relationFields;
    }

    public void setRelationFields(Set<String> relationFields) {
        this.relationFields = relationFields;
    }

    public Map<String, String> getModelField4RefModelField() {
        return modelField4RefModelField;
    }

    public void setModelField4RefModelField(Map<String, String> modelField4RefModelField) {
        this.modelField4RefModelField = modelField4RefModelField;
    }

    public String getDictDao() {
        return dictDao;
    }

    public void setDictDao(String dictDao) {
        this.dictDao = dictDao;
    }

    public Map<String, String> getRefDict2Dictid() {
        return refDict2Dictid;
    }

    public void setRefDict2Dictid(Map<String, String> refDict2Dictid) {
        this.refDict2Dictid = refDict2Dictid;
    }

    public Map<String, String> getDictId2RefDict() {
        return dictId2RefDict;
    }

    public void setDictId2RefDict(Map<String, String> dictId2RefDict) {
        this.dictId2RefDict = dictId2RefDict;
    }

    public String getRefExposeField() {
        return refExposeField;
    }

    public void setRefExposeField(String refExposeField) {
        this.refExposeField = refExposeField;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }

	public String getRefMappingModel() {
		return refMappingModel;
	}

	public void setRefMappingModel(String refMappingModel) {
		this.refMappingModel = refMappingModel;
	}

	public Map<String, String> getRefMappingField2MainModelField() {
		return refMappingField2MainModelField;
	}

	public void setRefMappingField2MainModelField(
			Map<String, String> refMappingField4MainModelField) {
		this.refMappingField2MainModelField = refMappingField4MainModelField;
	}

	public Map<String, String> getRefMappingField2SubModelField() {
		return refMappingField2SubModelField;
	}

	public void setRefMappingField2SubModelField(
			Map<String, String> refMappingField4SubModelField) {
		this.refMappingField2SubModelField = refMappingField4SubModelField;
	}

	public Map<String, String> getField2mappingMainModel() {
		return field2mappingMainModel;
	}

	public void setField2mappingMainModel(Map<String, String> field4mappingMainModel) {
		this.field2mappingMainModel = field4mappingMainModel;
	}

	public Map<String, String> getField2mappingSubModel() {
		return field2mappingSubModel;
	}

	public void setField2mappingSubModel(Map<String, String> field4mappingSubModel) {
		this.field2mappingSubModel = field4mappingSubModel;
	}

	public Map<String, String> getSubModelField2refMappingField() {
		return subModelField2refMappingField;
	}

	public void setSubModelField2refMappingField(
			Map<String, String> subModelField2refMappingField) {
		this.subModelField2refMappingField = subModelField2refMappingField;
	}

	public Map<String, String> getSubModelIdField2subModelField() {
		return subModelIdField2subModelField;
	}

	public void setSubModelIdField2subModelField(
			Map<String, String> subModelIdField2subModelField) {
		this.subModelIdField2subModelField = subModelIdField2subModelField;
	}

	public Map<String, String> getSubModelIdField2subModelType() {
		return subModelIdField2subModelType;
	}

	public void setSubModelIdField2subModelType(
			Map<String, String> subModelIdField2subModelType) {
		this.subModelIdField2subModelType = subModelIdField2subModelType;
	}

	public Map<String, String> getSubModelField2subModelType() {
		return subModelField2subModelType;
	}

	public Map<String, String> getSubModelType2subModelField() {
		return subModelType2subModelField;
	}

	public void setSubModelType2subModelField(
			Map<String, String> subModelType2subModelField) {
		this.subModelType2subModelField = subModelType2subModelField;
	}

	public void setSubModelField2subModelType(
			Map<String, String> subModelField2subModelType) {
		this.subModelField2subModelType = subModelField2subModelType;
	}

}
