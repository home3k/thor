/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.meta;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * modelContext------------------|
 * |                             |
 * |                             |
 * fieldContext, fieldContext    |
 * |         |
 * |         |
 * modelContext
 * <p/>
 * 目前不支持多层嵌套.
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ModelContext {

    /****************Start Meta field *****************/
    /**
     * name: Model名称
     */
    private String name;
    /**
     * desc: 描述信息
     */
    private String desc;

    /**
     * 操作信息
     * op:
     * - add
     * - mod
     */
    private List<String> op = Arrays.asList("add", "mod", "del", "query");
    /**
     * 是否自动存储
     */
    private Boolean autostore;
    /**
     * field字段信息
     * field:
     */
    private List<FieldContext> field;
    /**
     * model的pk字段
     */
    private String pk;
    /**
     * 条件字段.
     */
    private List<String> condition;

    /****************End Meta field *****************/

    /**
     * 其父节点。
     */
    private ModelContext parent;

    /**
     * @newtable属性的字段
     */
    private List<FieldContext> newTableField = new ArrayList<FieldContext>();

    /**
     * @refmodel属性的字段
     */
    private List<FieldContext> refModelField = new ArrayList<FieldContext>();
    
    private List<FieldContext> refMappingModelField = new ArrayList<FieldContext>();
    
	private Map<String, FieldContext> refModel4Field = new HashMap<String, FieldContext>();

    /**
     * @refdict属性的字段
     */
    private List<FieldContext> refDictField = new ArrayList<FieldContext>();

    private Map<String, FieldContext> refDict4Field = new HashMap<String, FieldContext>();

    /**
     * pk字段的meta信息
     */
    private FieldContext pkField;
    /**
     * 子model与父model关联的field
     */
    private FieldContext refPkField;

    /**
     * 是否是子Context
     */
    private Boolean child = false;
    /**
     * field -> fieldcontext
     */
    private Map<String, FieldContext> field4meta = new HashMap<String, FieldContext>();
    /**
     * condition -> fieldcontext
     */
    private Map<String, FieldContext> condfield4meta = new HashMap<String, FieldContext>();
    /**
     * table -> [fieldcontext...]
     */
    private Map<String, List<FieldContext>> table4meta = new HashMap<String, List<FieldContext>>();
    /**
     * Newtable option: table -> [fieldcontext...]
     */
    private Map<String, List<FieldContext>> table4newTablemeta = new HashMap<String, List<FieldContext>>();
    /**
     * newTableField -> table
     */
    private Map<FieldContext, String> newTableMeta4table = new HashMap<FieldContext, String>();

    public Boolean getAutostore() {
        return autostore;
    }

    public void setAutostore(Boolean autostore) {
        this.autostore = autostore;
    }

    public void refresh(ApplicationContext context) {
        List<FieldContext> iteFields = new ArrayList<FieldContext>();
        iteFields.addAll(field);
        for (FieldContext fieldContext : iteFields) {
            fieldContext.setParentContext(this);
            fieldContext.refresh(context);
            field4meta.put(fieldContext.getName(), fieldContext);
        }

        refreshDict();
        if (condition != null) {
            for (String field : condition) {
                FieldContext fieldContext = field4meta.get(field);
                if (fieldContext == null) {
                    fieldContext = new FieldContext();
                    fieldContext.setName(field);
                    fieldContext.setDesc("特殊查询");
                }
                condfield4meta.put(field, fieldContext);
            }
        }

        List<FieldContext> inFields = new ArrayList<FieldContext>();

        for (FieldContext fieldContext : field) {
            if (fieldContext.getExclude()) {
                field4meta.remove(fieldContext.getName());
            } else {
                inFields.add(fieldContext);
            }
        }

        field = inFields;

        refreshTableMeta();
        refreshNewTableMeta();
    }

    private void refreshDict() {
        for (FieldContext fieldContext : refDictField) {
            if (StringUtils.isNotBlank(fieldContext.getRefExposeField())) {
                FieldContext expose = field4meta.get(fieldContext.getRefExposeField());
                expose.getRelationFields().add(fieldContext.getRefDict2Dictid().get(fieldContext.getName()));
            }
        }
    }

    private void refreshTableMeta() {
        for (FieldContext fieldContext : field) {
            if (!fieldContext.isStore()) {
                continue;
            }
            String table = fieldContext.getFieldStore().getTable();
            List<FieldContext> fieldContextList = table4meta.get(table);
            if (fieldContextList == null)
                fieldContextList = new ArrayList<FieldContext>();
            fieldContextList.add(fieldContext);
            table4meta.put(table, fieldContextList);
        }
    }

    private void refreshNewTableMeta() {
        for (FieldContext fieldContext : newTableField) {
            ModelContext context = fieldContext.getContext();
            if (context == null) {
                continue;
            }
            for (FieldContext subFieldContext : context.getField()) {
                String table = subFieldContext.getFieldStore().getTable();
                // submodel默认只能对应一个table.
                this.newTableMeta4table.put(fieldContext, table);
                List<FieldContext> fieldContextList = table4newTablemeta.get(table);
                if (fieldContextList == null)
                    fieldContextList = new ArrayList<FieldContext>();
                fieldContextList.add(subFieldContext);
                table4newTablemeta.put(table, fieldContextList);
            }
        }
    }

    public Map<String, FieldContext> getField4meta() {
        return field4meta;
    }

    public void setField4meta(Map<String, FieldContext> field4meta) {
        this.field4meta = field4meta;
    }

    public List<String> getCondition() {
        return condition;
    }

    public void setCondition(List<String> condition) {
        this.condition = condition;
    }

    public Map<String, FieldContext> getCondfield4meta() {
        return condfield4meta;
    }

    public void setCondfield4meta(Map<String, FieldContext> condfield4meta) {
        this.condfield4meta = condfield4meta;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getOp() {
        return op;
    }

    public void setOp(List<String> op) {
        this.op = op;
    }

    public List<FieldContext> getField() {
        return field;
    }

    public void setField(List<FieldContext> field) {
        this.field = field;
    }
    
    public FieldContext getFieldContext(String field) {
    	for (FieldContext fieldContext : this.field) {
    		if (fieldContext.getName().equals(field)) {
    			return fieldContext;
    		}
    	}
    	return null;
    }
    
    public FieldContext getFieldContextWithMapping(String field) {
    	for (FieldContext fieldContext : this.field) {
    		if (fieldContext.getRefMappingField2MainModelField().keySet().contains(field)) {
    			return fieldContext;
    		}
    	}
    	return null;
    }
    
    public ModelContext getParent() {
        return parent;
    }

    public void setParent(ModelContext parent) {
        this.parent = parent;
    }

    public ModelContext getRoot() {
        if (this.parent == null) {
            return this;
        } else {
            ModelContext pre = this.parent;
            ModelContext current = pre.getParent();
            while (current != null) {
                pre = current;
                current = current.getParent();
            }
            return pre;
        }
    }

    public List<FieldContext> getNewTableField() {
        return newTableField;
    }

    public void setNewTableField(List<FieldContext> newTableField) {
        this.newTableField = newTableField;
    }

    public FieldContext getPkField() {
        return pkField;
    }

    public void setPkField(FieldContext pkField) {
        this.pkField = pkField;
    }

    public FieldContext getRefPkField() {
        return refPkField;
    }

    public void setRefPkField(FieldContext refPkField) {
        this.refPkField = refPkField;
    }

    public Boolean getChild() {
        return child;
    }

    public void setChild(Boolean child) {
        this.child = child;
    }

    public Map<String, List<FieldContext>> getTable4meta() {
        return table4meta;
    }

    public void setTable4meta(Map<String, List<FieldContext>> table4meta) {
        this.table4meta = table4meta;
    }

    public Map<String, List<FieldContext>> getTable4newTablemeta() {
        return table4newTablemeta;
    }

    public void setTable4newTablemeta(Map<String, List<FieldContext>> table4newTablemeta) {
        this.table4newTablemeta = table4newTablemeta;
    }

    public Map<FieldContext, String> getNewTableMeta4table() {
        return newTableMeta4table;
    }

    public void setNewTableMeta4table(Map<FieldContext, String> newTableMeta4table) {
        this.newTableMeta4table = newTableMeta4table;
    }

    public List<FieldContext> getRefModelField() {
        return refModelField;
    }

    public void setRefModelField(List<FieldContext> refModelField) {
        this.refModelField = refModelField;
    }

    public List<FieldContext> getRefMappingModelField() {
		return refMappingModelField;
	}

	public void setRefMappingModelField(List<FieldContext> refMappingModelField) {
		this.refMappingModelField = refMappingModelField;
	}

	public Map<String, FieldContext> getRefModel4Field() {
        return refModel4Field;
    }

    public void setRefModel4Field(Map<String, FieldContext> refModel4Field) {
        this.refModel4Field = refModel4Field;
    }

    public List<FieldContext> getRefDictField() {
        return refDictField;
    }

    public void setRefDictField(List<FieldContext> refDictField) {
        this.refDictField = refDictField;
    }

    public Map<String, FieldContext> getRefDict4Field() {
        return refDict4Field;
    }

    public void setRefDict4Field(Map<String, FieldContext> refDict4Field) {
        this.refDict4Field = refDict4Field;
    }

    /**
     * 字典类，可以签到model
     */
    private List<ModelContext> dict;

    private Map<String, ModelContext> name4dict;

    public List<ModelContext> getDict() {
        return dict;
    }

    public void setDict(List<ModelContext> dict) {
        this.dict = dict;

        name4dict = new HashMap<String, ModelContext>();
        for (ModelContext context : dict) {
            name4dict.put(context.getName(), context);
        }
    }

    public Map<String, ModelContext> getName4dict() {
        return name4dict;
    }

    public void setName4dict(Map<String, ModelContext> name4dict) {
        this.name4dict = name4dict;
    }

    private String dictModel;

    public String getDictModel() {
        return dictModel;
    }

    public void setDictModel(String dictModel) {
        this.dictModel = dictModel;
    }
    
}

