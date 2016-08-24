/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.dal;

import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import freemarker.template.*;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k
 */
@Service
public class DaoGenerator extends AbstractCodegenerator implements CodeGenerator {

    private String base = "dao-gen";

    private String templateDir = "template/dal";

    private String boTemplate = "Bo.ftl";

    private String columnEnumTemplate = "Column.ftl";

    private String rowMapperTemplate = "RowMapper.ftl";

    private String columnRowMapperTemplate = "ColumnRowMapper.ftl";

    private String daoTemplate = "Dao.ftl";
    private String daoImplTemplate = "DaoImpl.ftl";

    private String currentDir = "thor";

    private String database = "wmkq";



    protected String getCodegenBasePath() {
        return "thor-dal/src/main/java/com/haoyayi/thor/dal";
    }

    public void _generateCode(ModelContext context, Set<String> actions) throws Exception {
        currentDir = "thor";
        generateCode("wmkq", datasourcethor, actions);
    }

    private void generateCode(String db, DataSource datasource, Set<String> genTables) throws Exception {
        Connection connection = datasource.getConnection();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet tables = meta.getTables(db, db, null, new String[]{"TABLE"});
        Set<String> tablenames = new HashSet<String>();
        Map<String, Map<String, String>> table2field = new HashMap<String, Map<String, String>>();
        Map<String, String> table2pk = new HashMap<String, String>();
        while (tables.next()) {
            tablenames.add(tables.getString("TABLE_NAME"));
        }
        for (String table : tablenames) {

            // fetch table pk.
            ResultSet rs = meta.getPrimaryKeys(db, db, table);
            while (rs.next()) {
                String pk = rs.getString("COLUMN_NAME");
                table2pk.put(table, pk);
                System.out.println(table + ". pk->" + pk);
            }


            ResultSet columns = meta.getColumns(db, db, table, null);
            Map<String, String> field2type = new HashMap<String, String>();
            while (columns.next()) {
                String fieldname = columns.getString("COLUMN_NAME");
                String typeclass = dbConf.getTypemap().get(columns.getString("DATA_TYPE"));
                // System.out.println(table+","+fieldname+","+columns.getString("DATA_TYPE"));
                field2type.put(fieldname, typeclass);
            }
            String realtable = table.replaceAll("[0-9]", "");
            if (genTables.contains(realtable))
                table2field.put(realtable, field2type);
        }

        for (String table : table2field.keySet()) {
            generateFieldEnum(table, table2field.get(table));
            generateBo(table, table2field.get(table), table2pk.get(table));
            generateRowMapper(table, table2field.get(table));
            generateColumnRowMapper(table, table2field.get(table));
            generateDao(table);
            generateDaoImpl(table, table2pk.get(table));
        }

    }

    private void generateDao(String tablename) throws IOException, TemplateException {
        Map<String, String> data = new HashMap<String, String>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("Classname", CamelUtils.upperFirst(classname));

        generateFile(base, "dal/" + getDaoTemplate(), CamelUtils.upperFirst(classname) + "DAO.java", data, "");

    }

    private void generateDaoImpl(String tablename, String pk) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap<String, Object>();
        String classname = CamelUtils.getCamelStr(tablename);
        data.put("classname", classname);
        data.put("database", this.database);
        data.put("tablename", tablename);
        data.put("pk", CamelUtils.getCamelStr(pk));
        data.put("Classname", CamelUtils.upperFirst(classname));
        data.put("del", false);

        generateFile(base, "dal/" + getDaoImplTemplate(), CamelUtils.upperFirst(classname) + "DAOImpl.java", data, "impl");

    }

    private void generateColumnRowMapper(String tablename, Map<String, String> columns) throws IOException, TemplateException {
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
            setStr.append("\"));\n\t\t}\n\t\t");
        }
        data.put("setStr", setStr.toString());
        generateFile(base, "dal/" + getColumnRowMapperTemplate(), CamelUtils.upperFirst(classname) + "ColumnRowMapper.java", data, "columnrowmapper");
    }


    private void generateRowMapper(String tablename, Map<String, String> columns) throws IOException, TemplateException {
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
            setStr.append("\"))");
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




    public DBDesc getDbConf() {
        return dbConf;
    }

    public void setDbConf(DBDesc dbConf) {
        this.dbConf = dbConf;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
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


    public static void main(String[] args) {
        System.out.println(CamelUtils.getCamelStr("hello_world"));
        System.out.println(CamelUtils.getCamelStr("hello__world"));
        System.out.println(CamelUtils.getCamelStr("_hello__world"));
        System.out.println(CamelUtils.getCamelStr("_hello__World"));
        System.out.println(CamelUtils.getCamelStr("___hello__World"));
    }

}
