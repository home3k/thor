/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.biz;

import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.meta.BoField;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class BizCodeGenerator extends AbstractCodegenerator implements CodeGenerator {

    private static final String ACTION_APIIMPL = "apiImpl";
    private static final String ACTION_BIZ = "biz";
    private static final String ACTION_FACTORY = "factory";
    private static final String ACTION_REPOSITORY = "repository";
    private static final String ACTION_VALIDATOR = "validator";
    private static final String ACTION_SERVICE = "service";
    private static final String ACTION_CONVERTBIZ = "convertBiz";
    private static final String ACTION_QUERYBIZ = "queryBiz";


    private String base = "biz";

    @Override
    protected String getCodegenBasePath() {
        return "thor-biz/src/main/java/com/haoyayi/thor";
    }


    @Override
    public void _generateCode(ModelContext context, Set<String> actions) throws Exception {
        if (actions.contains(ACTION_APIIMPL)) {
            this.generateAPIImpl(context);
        }
        if (actions.contains(ACTION_BIZ)) {
            this.generateBiz(context);
        }
        if (actions.contains(ACTION_QUERYBIZ)) {
            this.generateQueryBiz(context);
        }
        if (actions.contains(ACTION_FACTORY)) {
            this.generateFactory(context);
        }
        if (actions.contains(ACTION_REPOSITORY)) {
            this.generateRepository(context);
        }
        if (actions.contains(ACTION_VALIDATOR)) {
            this.generateValidator(context);
        }
        if (actions.contains(ACTION_SERVICE)) {
            this.generateService(context);
        }
        if (actions.contains(ACTION_CONVERTBIZ)) {
            this.generateConvertBiz(context);
        }
    }

    private void generateConvertBiz(ModelContext context) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));

        generateFile(base, getFtlPathPrefix() + "biz/" + "ConvertBiz.ftl", CamelUtils.upperFirst(context.getName()) + "ConvertBiz.java", data, "/biz/" + getFtlPathPrefix(context.getName()));

    }

    private void generateService(ModelContext context) throws Exception {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("model", context.getName());
        data.put("Model", CamelUtils.upperFirst(context.getName()));
        data.put("pk", context.getPk());

        generateFile(base, getFtlPathPrefix() + "biz/" + "Service.ftl", CamelUtils.upperFirst(context.getName()) + "Service.java", data, "/service/" + getFtlPathPrefix(context.getName()));

    }


    private void generateBiz(ModelContext context) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));

        generateFile(base, getFtlPathPrefix() + "biz/" + "Biz.ftl", CamelUtils.upperFirst(context.getName()) + "Biz.java", data, "/biz/" + getFtlPathPrefix(context.getName()));

    }

    private void generateQueryBiz(ModelContext context) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));

        generateFile(base, getFtlPathPrefix() + "biz/" + "QueryBiz.ftl", CamelUtils.upperFirst(context.getName()) + "QueryBiz.java", data, "/biz/" + getFtlPathPrefix(context.getName()));

    }

    private void generateAPIImpl(ModelContext context) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));
        data.put("pk", context.getPk());
        generateFile(base, getFtlPathPrefix() + "biz/" + "ModAPIImpl.ftl", CamelUtils.upperFirst(context.getName()) + "ModAPIImpl.java", data, "/impl/" + getFtlPathPrefix(context.getName()));
        generateFile(base, getFtlPathPrefix() + "biz/" + "QueryAPIImpl.ftl", CamelUtils.upperFirst(context.getName()) + "QueryAPIImpl.java", data, "/impl/" + getFtlPathPrefix(context.getName()));

    }

    private void refreshModel(List<FieldContext> fieldContexts, Map<String, Object> data) {
        data.put("newDate", false);
        data.put("updateDate", false);
        data.put("isDel", false);

        for (FieldContext fieldContext : fieldContexts) {
            if (fieldContext.hasOption() && fieldContext.getFieldOption().getNewdate()) {
                data.put("newDate", true);
                data.put("NEWDATE", CamelUtils.upperFirst(fieldContext.getName()));
            }
            if (fieldContext.hasOption() && fieldContext.getFieldOption().getUpdateDate()) {
                data.put("updateDate", true);
                data.put("UPDATEDATE", CamelUtils.upperFirst(fieldContext.getName()));
            }
            if (fieldContext.hasOption() && fieldContext.getFieldOption().getDel()) {
                data.put("isDel", true);
                data.put("ISDEL", CamelUtils.upperFirst(fieldContext.getName()));
            }

        }
    }

    private void generateFactory(ModelContext context) throws Exception {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));
        data.put("pk", context.getPk());
        data.put("Pk", CamelUtils.upperFirst(context.getPk()));
        data.put("autokey", context.getPkField().hasOption() && context.getPkField().getFieldOption().getAutoKey());

        refreshModel(context.getField(), data);

        generateFile(base, getFtlPathPrefix() + "biz/" + "ModelFactory.ftl", CamelUtils.upperFirst(context.getName()) + "ModelFactory.java", data, "/factory/" + getFtlPathPrefix(context.getName()));

    }

    private void generateRepository(ModelContext context) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));
        data.put("pk", context.getPk());

        data.put("bolist", generateBoField(context.getTable4meta()));
        data.put("newtablelist", generateBoField(context.getNewTableField(), context.getNewTableMeta4table(), context.getTable4newTablemeta()));

        String table = context.getPkField().getFieldStore().getTable();
        data.put("Mainbo", CamelUtils.upperFirst(CamelUtils.getCamelStr(table)));
        data.put("mainbo", CamelUtils.getCamelStr(table));

        data.put("modelAutokey", context.getPkField().hasOption() && context.getPkField().getFieldOption().getAutoKey());

        generateFile(base, getFtlPathPrefix() + "biz/" + "Repository.ftl", CamelUtils.upperFirst(context.getName()) + "Repository.java", data, "/repository/" + getFtlPathPrefix(context.getName()));

    }

    private void generateValidator(ModelContext context) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));
        data.put("pk", context.getPk());

        generateFile(base, getFtlPathPrefix() + "biz/" + "Validator.ftl", CamelUtils.upperFirst(context.getName()) + "Validator.java", data, "/validate/" + getFtlPathPrefix(context.getName()));

    }

    private List<BoField> generateBoField(Map<String, List<FieldContext>> context) {
        List<BoField> result = new ArrayList<BoField>();
        for (String table : context.keySet()) {
            List<FieldContext> fieldContexts = context.get(table);
            BoField boField = new BoField();
            boField.setBo(CamelUtils.getCamelStr(table));
            boField.setBO(CamelUtils.upperFirst(CamelUtils.getCamelStr(table)));
            for (FieldContext fieldContext : fieldContexts) {
                if (fieldContext.hasOption() && fieldContext.getFieldOption().getNewdate()) {
                    boField.setNewDate(true);
                    boField.setDate(fieldContext.getName());
                    boField.setDATE(CamelUtils.upperFirst(fieldContext.getName()));
                }
                if (fieldContext.hasOption() && fieldContext.getFieldOption().getUpdateDate()) {
                    boField.setUpdateDate(true);
                    boField.setDate(fieldContext.getName());
                    boField.setDATE(CamelUtils.upperFirst(fieldContext.getName()));
                }
                if (fieldContext.hasOption() && fieldContext.getFieldOption().getAutoKey()) {
                    boField.setAutoKey(true);
                }
            }
            result.add(boField);
        }
        return result;
    }

    private List<BoField> generateBoField(List<FieldContext> newTableFields, Map<FieldContext, String> context, Map<String, List<FieldContext>> tncontext) {
        List<BoField> result = new ArrayList<BoField>();
        for (FieldContext newTableField : newTableFields) {

            BoField boField = new BoField();
            String table = context.get(newTableField);
            boField.setBo(CamelUtils.getCamelStr(table));
            boField.setBO(CamelUtils.upperFirst(CamelUtils.getCamelStr(table)));
            boField.setIsArray((newTableField.hasOption() && newTableField.getFieldOption().getArray()));
            String model = newTableField.getContext().getName();
            boField.setModel(CamelUtils.getCamelStr(model));
            boField.setMODEL(CamelUtils.upperFirst(CamelUtils.getCamelStr(model)));
            List<FieldContext> fieldContexts = tncontext.get(table);
            for (FieldContext fieldContext : fieldContexts) {
                if (fieldContext.hasOption() && fieldContext.getFieldOption().getNewdate()) {
                    boField.setNewDate(true);
                    boField.setDate(fieldContext.getName());
                    boField.setDATE(CamelUtils.upperFirst(fieldContext.getName()));
                }
                if (fieldContext.hasOption() && fieldContext.getFieldOption().getUpdateDate()) {
                    boField.setUpdateDate(true);
                    boField.setDate(fieldContext.getName());
                    boField.setDATE(CamelUtils.upperFirst(fieldContext.getName()));
                }
                if (fieldContext.hasOption() && fieldContext.getFieldOption().getAutoKey()) {
                    boField.setAutoKey(true);
                }
            }
            boField.setRefpk(newTableField.getContext().getRefPkField().getName());
            boField.setREFPK(CamelUtils.upperFirst(newTableField.getContext().getRefPkField().getName()));
            result.add(boField);
        }
        return result;
    }
}
