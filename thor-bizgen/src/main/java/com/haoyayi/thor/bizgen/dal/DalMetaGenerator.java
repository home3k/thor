/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.dal;

import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.GenType;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class DalMetaGenerator extends AbstractCodegenerator implements CodeGenerator {

    private String base = "dao-gen";

    private String boTemplate = "Bo.ftl";

    private String columnEnumTemplate = "Column.ftl";

    private String rowMapperTemplate = "RowMapper.ftl";

    private String columnRowMapperTemplate = "ColumnRowMapper.ftl";

    private String daoTemplate = "Dao.ftl";
    private String daoImplTemplate = "DaoImpl.ftl";

    private String currentDir = "thor";

    private static final String ACTION_DAO = "dao";
    private static final String ACTION_DAOIMPL = "daoImpl";
    private static final String ACTION_COLUMNRM = "columnRowMapper";
    private static final String ACTION_RM = "rowMapper";
    private static final String ACTION_COLUMN = "column";
    private static final String ACTION_BO = "bo";

    @Override
    protected String getCodegenBasePath() {
        return "thor-dal/src/main/java/com/haoyayi/thor/dal";
    }

    private String getRowMappperType(String type, FieldContext fieldContext) {
        if (getRowMappperBoolean(type, fieldContext)) {
            return "Integer";
        } else {
            return type;
        }
    }

    private Boolean getRowMappperBoolean(String type, FieldContext fieldContext) {
        if (type.equals("Boolean")) {
            return true;
        } else {
            return false;
        }
    }

    private String getIsdelField(List<FieldContext> fieldContexts) {
        for (FieldContext fieldContext : fieldContexts) {
            if (fieldContext.hasOption() && fieldContext.getFieldOption().getDel()) {
                return fieldContext.getName();
            }
        }
        return null;
    }

    @Override
    public void _generateCode(ModelContext context, Set<String> actions) throws Exception {
        Map<String, String> col4type = new HashMap<String, String>();
        Map<String, String> col4rowtype = new HashMap<String, String>();
        Map<String, Boolean> col4boolOption = new HashMap<String, Boolean>();
        for (String table : context.getTable4meta().keySet()) {
            List<FieldContext> fieldContextList = context.getTable4meta().get(table);
            if (actions.contains(ACTION_DAO)) {
                generateDao(table);
            }
            if (actions.contains(ACTION_DAOIMPL)) {
                generateDaoImpl(context, table, context.getPkField().getFieldStore().getDb(), CamelUtils.getUnCamelStr(context.getPk()), getIsdelField(fieldContextList));
            }
            for (FieldContext fieldContext : fieldContextList) {
                String column = CamelUtils.getUnCamelStr(fieldContext.getName());
                String type = fieldContext.getFieldType().getJavaType();
                col4type.put(column, type);
                col4rowtype.put(column, getRowMappperType(type, fieldContext));
                col4boolOption.put(column, getRowMappperBoolean(type, fieldContext));
            }

            if (actions.contains(ACTION_BO)) {
                generateBo(table, col4type, CamelUtils.getUnCamelStr(context.getPk()));
            }
            if (actions.contains(ACTION_COLUMN)) {
                generateFieldEnum(table, col4type);
            }
            if (actions.contains(ACTION_COLUMNRM)) {
                generateColumnRowMapper(table, col4rowtype, col4boolOption);
            }
            if (actions.contains(ACTION_RM)) {
                generateRowMapper(table, col4rowtype, col4boolOption);
            }
        }

        col4type.clear();
        col4rowtype.clear();
        col4boolOption.clear();
        for (FieldContext subFieldContext : context.getNewTableField()) {
            String table = context.getNewTableMeta4table().get(subFieldContext);
            List<FieldContext> fieldContextList = context.getTable4newTablemeta().get(table);
            if (actions.contains(ACTION_DAO)) {
                generateDao(table);
            }
            ModelContext subContext = subFieldContext.getContext();
            if (actions.contains(ACTION_DAOIMPL)) {
                generateDaoImpl(context, table, subContext.getPkField().getFieldStore().getDb(), CamelUtils.getUnCamelStr(subContext.getPk()), getIsdelField(fieldContextList));
            }
            for (FieldContext fieldContext : fieldContextList) {
                String column = CamelUtils.getUnCamelStr(fieldContext.getName());
                String type = fieldContext.getFieldType().getJavaType();
                col4type.put(column, type);
                col4rowtype.put(column, getRowMappperType(type, fieldContext));
                col4boolOption.put(column, getRowMappperBoolean(type, fieldContext));
            }
            if (actions.contains(ACTION_BO)) {
                generateBo(table, col4type, CamelUtils.getUnCamelStr(subContext.getPk()));
            }
            if (actions.contains(ACTION_COLUMNRM)) {
                generateColumnRowMapper(table, col4rowtype, col4boolOption);
            }
            if (actions.contains(ACTION_COLUMN)) {
                generateFieldEnum(table, col4type);
            }
            if (actions.contains(ACTION_RM)) {
                generateRowMapper(table, col4rowtype, col4boolOption);
            }
        }
    }

    private void generateDao(String tablename) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap<String, Object>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("Classname", CamelUtils.upperFirst(classname));
        if (getGenType()== GenType.dict) {
            data.put("dict",true);
        } else {
        	data.put("dict", false);
        }
        generateFile(base, "dal/" + getDaoTemplate(), CamelUtils.upperFirst(classname) + "DAO.java", data, "");

    }

    private String getDictField(ModelContext context) {
        for (FieldContext fieldContext : context.getField()) {
            if (fieldContext.hasOption() && fieldContext.getFieldOption().getDict()) {
                return fieldContext.getName();
            }
        }
        return "";
    }

    private void generateDaoImpl(ModelContext context, String tablename, String database, String pk, String isdelField) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap<String, Object>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("database", database);
        data.put("tablename", tablename);
        data.put("pk", CamelUtils.getCamelStr(pk));
        data.put("Classname", CamelUtils.upperFirst(classname));
        data.put("del", isdelField != null);
        data.put("isdel", isdelField);
        if (getGenType()== GenType.dict) {
            data.put("dict",true);
            data.put("dictField", getDictField(context));
        } else {
        	data.put("dict", false);
        }
        generateFile(base, "dal/" + getDaoImplTemplate(), CamelUtils.upperFirst(classname) + "DAOImpl.java", data, "impl");

    }

    private void generateColumnRowMapper(String tablename, Map<String, String> columns, Map<String, Boolean> col4bool) throws IOException, TemplateException {
        Map<String, String> data = new HashMap<String, String>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("Classname", CamelUtils.upperFirst(classname));
        StringBuilder setStr = new StringBuilder();
        for (String key : columns.keySet()) {
            String field = CamelUtils.getCamelStr(key);
            setStr.append("if(columns.contains(");
            setStr.append(CamelUtils.upperFirst(classname));
            setStr.append("Column." + field + ")){\n\t\t\t");
            setStr.append("items.put(");
            setStr.append(CamelUtils.upperFirst(classname));
            setStr.append("Column.");
            setStr.append(field);
            setStr.append(", rs.get");
            String type = columns.get(key);
            type = type.substring(type.lastIndexOf(".") + 1);
            if (type.equals("Integer")) {
                type = "Int";
            }
            if (type.equals("Date")) {
                type = "Timestamp";
            }
            setStr.append(type);
            setStr.append("(\"");
            setStr.append(key);
            setStr.append("\")");
            if (col4bool.get(key)) {
                setStr.append(">0?Boolean.TRUE:Boolean.FALSE");
            }
            setStr.append(");\n\t\t}\n\t\t");
        }
        data.put("setStr", setStr.toString());
        generateFile(base, "dal/" + getColumnRowMapperTemplate(), CamelUtils.upperFirst(classname) + "ColumnRowMapper.java", data, "columnrowmapper");
    }


    private void generateRowMapper(String tablename, Map<String, String> columns, Map<String, Boolean> col4bool) throws IOException, TemplateException {
        Map<String, String> data = new HashMap<String, String>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("Classname", CamelUtils.upperFirst(classname));
        StringBuilder setStr = new StringBuilder();
        for (String key : columns.keySet()) {
            String field = CamelUtils.getCamelStr(key);
            setStr.append(classname);
            setStr.append(".set");
            setStr.append(field.replaceFirst(key.substring(0, 1), field.substring(0, 1).toUpperCase()));
            setStr.append("(rs.get");
            String type = columns.get(key);
            if (type.contains("."))
                type = type.substring(type.lastIndexOf(".") + 1);
            if (type.equals("Integer")) {
                type = "Int";
            }
            if (type.equals("Date")) {
                type = "Timestamp";
            }
            setStr.append(type);
            setStr.append("(\"");
            setStr.append(key);
            setStr.append("\")");
            if (col4bool.get(key)) {
                setStr.append(">0?Boolean.TRUE:Boolean.FALSE");
            }
            setStr.append(")");
            setStr.append(";\n\t\t");
        }
        data.put("setStr", setStr.toString());
        generateFile(base, "dal/" + getRowMapperTemplate(), CamelUtils.upperFirst(classname) + "RowMapper.java", data, "rowmapper");
    }

    private void generateFieldEnum(String tablename, Map<String, String> columns) throws TemplateException, IOException {

        Map<String, String> data = new HashMap<String, String>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("Classname", CamelUtils.upperFirst(classname));
        data.put("classname", classname);
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String field : columns.keySet()) {
            index++;
            sb.append("@FieldQualifier(\"").append(field).append("\")\n");
            sb.append("\t").append(CamelUtils.getCamelStr(field));
            if (index != columns.size()) {
                sb.append(",").append("\n").append("\n\t");
            }
        }
        data.put("setStr", sb.toString());
        generateFile(base, "dal/" + getColumnEnumTemplate(), CamelUtils.upperFirst(classname) + "Column.java", data, "meta");
    }

    private void generateBo(String tablename, Map<String, String> columns, String pk) throws IOException, TemplateException {
        System.out.println(tablename + " PK" + pk);
        HashMap<String, String> data = new HashMap<String, String>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("Classname", CamelUtils.upperFirst(classname));
        data.put("pk", CamelUtils.getCamelStr(pk));
        StringBuffer sb = new StringBuffer();
        for (String field : columns.keySet()) {
            sb.append("@FieldQualifier(\"").append(field).append("\")\n");
            sb.append("\tprivate ");
            sb.append(columns.get(field));
            sb.append(" ");
            sb.append(CamelUtils.getCamelStr(field));
            sb.append(";\n");
            sb.append("\n\t");
        }

        for (String field : columns.keySet()) {
//			public java.lang.Long getCycid() {
//				return cycid;
//			}
//			public void setCycid(java.lang.Long cycid) {
//				this.cycid = cycid;
//			}
            sb.append("public ");
            sb.append(columns.get(field));
            sb.append(" get");
            sb.append(CamelUtils.upperFirst(CamelUtils.getCamelStr(field)));
            sb.append("() {\n\t\treturn ");
            sb.append(CamelUtils.getCamelStr(field));
            sb.append(";\n\t}");
            sb.append("\n\t");

            sb.append("public void set");
            sb.append(CamelUtils.upperFirst(CamelUtils.getCamelStr(field)));
            sb.append("(" + columns.get(field) + " " + CamelUtils.getCamelStr(field) + " ) {\n\t\tthis. ");
            sb.append(CamelUtils.getCamelStr(field));
            sb.append(" = ");
            sb.append(CamelUtils.getCamelStr(field));
            sb.append(";\n\t}");
            sb.append("\n\t");
        }

        data.put("setStr", sb.toString());
        generateFile(base, "dal/" + getBoTemplate(), CamelUtils.upperFirst(classname) + ".java", data, "bo");
    }

    public String getBoTemplate() {
        return boTemplate;
    }

    public void setBoTemplate(String boTemplate) {
        this.boTemplate = boTemplate;
    }

    public String getColumnEnumTemplate() {
        return columnEnumTemplate;
    }

    public void setColumnEnumTemplate(String columnEnumTemplate) {
        this.columnEnumTemplate = columnEnumTemplate;
    }

    public String getRowMapperTemplate() {
        return rowMapperTemplate;
    }

    public String getColumnRowMapperTemplate() {
        return columnRowMapperTemplate;
    }

    public void setRowMapperTemplate(String rowMapperTemplate) {
        this.rowMapperTemplate = rowMapperTemplate;
    }

    public String getDaoTemplate() {
        return daoTemplate;
    }

    public void setDaoTemplate(String daoTemplate) {
        this.daoTemplate = daoTemplate;
    }

    public String getDaoImplTemplate() {
        return daoImplTemplate;
    }

    public void setDaoImplTemplate(String daoImplTemplate) {
        this.daoImplTemplate = daoImplTemplate;
    }


}
