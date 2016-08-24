/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.api;

import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.meta.FieldContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ApiCodeGenerator extends AbstractCodegenerator implements CodeGenerator {

    private static final String ACTION_API = "api";

    private static final String ACTION_DTO = "dto";

    private String base = "api";

    @Override
    protected String getCodegenBasePath() {
        return "thor-api/src/main/java/com/haoyayi/thor/api";
    }

    @Override
    public void _generateCode(ModelContext context, Set<String> actions) throws Exception {
        if (actions.contains(ACTION_API)) {
            this.generateApi(context);
        }
        if (actions.contains(ACTION_DTO)) {
            this.generateDto(context);
        }
    }

    private void generateApi(ModelContext context) throws IOException, TemplateException {

        Map<String, String> data = new HashMap<String, String>();
        data.put("model", context.getName());
        data.put("desc", context.getDesc());
        data.put("Model", CamelUtils.upperFirst(context.getName()));

        generateFile(base, getFtlPathPrefix() + "api/ModAPI.ftl", CamelUtils.upperFirst(context.getName()) + "ModAPI.java", data, getFtlPathPrefix(context.getName()) + "/api");
        generateFile(base, getFtlPathPrefix() + "api/QueryAPI.ftl", CamelUtils.upperFirst(context.getName()) + "QueryAPI.java", data, getFtlPathPrefix(context.getName()) + "/api");

    }

    private String getIgnore(List<FieldContext> fieldContexts) {
        StringBuilder sb = new StringBuilder();
        sb.append("@JsonIgnoreProperties({\"declareFields\",\"refModelPk\",\"container\"");
        for (FieldContext fieldContext : fieldContexts) {
            sb.append(",\"").append(fieldContext.getName()).append("\"");
        }
        sb.append("})");
        return sb.toString();
    }

    private String genDesc(ModelContext context,String desc) {
        StringBuilder sb = new StringBuilder();
        sb.append(desc).append("\n\n");
        sb.append("Path:\n\t").append("/").append(context.getName());
        return sb.toString();
    }

    private void generateDto(ModelContext context) throws IOException, TemplateException {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("desc", context.getDesc());
        data.put("model", context.getName());
        if (context.getChild()) {
            data.put("pack", context.getRoot().getName());
        } else {
            data.put("pack", context.getName());
        }

        data.put("ignore", getIgnore(getNotExposeField(context.getField())));
        data.put("Model", CamelUtils.upperFirst(context.getName()));

        data.put("pk", context.getPk());

        data.put("fieldlist", context.getField());

        data.put("refmodellist", context.getRefModelField());

        generateFile(base, getFtlPathPrefix() + "api/" + (context.getChild() ? "Common" : "") + "TypeField.ftl", CamelUtils.upperFirst(context.getName()) + "TypeField.java", data, getFtlPathPrefix((context.getChild() ? context.getRoot().getName() : context.getName())) + "/dto");
        StringBuilder sb = new StringBuilder();
        for (FieldContext field : context.getField()) {
            if (field.getName().equals("id")) {
                continue;
            }
            sb.append("/**\n\t * ").append(field.getDesc());
            sb.append("\n\t */\n    ");
            sb.append("public ");
            sb.append(field.getFieldType().getJavaType());
            sb.append(" get");
            sb.append(CamelUtils.upperFirst(field.getName()));
            sb.append("() {\n\t\treturn ");
            sb.append(field.getName());
            sb.append(";\n\t}");
            sb.append("\n\t");

            sb.append("/**\n\t * ").append(field.getDesc()).append("\n\t */\n    ");
            sb.append("public void set");
            sb.append(CamelUtils.upperFirst(field.getName()));
            sb.append("(" + field.getFieldType().getJavaType() + " " + field.getName() + " ) {\n\t\tthis. ");
            sb.append(field.getName());
            sb.append(" = ");
            sb.append(field.getName());
            sb.append(";\n\t}");
            sb.append("\n\t");

            if (field.hasOption() && field.getFieldOption().getNewtable()) {
                generateDto(field.getContext());
            }
        }

        data.put("setStr", sb.toString());

        generateFile(base, getFtlPathPrefix() + "api/" + (context.getChild() ? "Common" : "") + "Type.ftl", CamelUtils.upperFirst(context.getName()) + "Type.java", data, getFtlPathPrefix((context.getChild() ? context.getRoot().getName() : context.getName())) + "/dto");

        data.put("fieldlist", context.getCondfield4meta().values());
        if (!context.getChild())
            generateFile(base, getFtlPathPrefix() + "api/" + "ConditionField.ftl", CamelUtils.upperFirst(context.getName()) + "ConditionField.java", data, getFtlPathPrefix(context.getName()) + "/dto");

    }

    private List<FieldContext> getNotExposeField(List<FieldContext> fieldContexts) {
        List<FieldContext> result = new ArrayList<FieldContext>();
        for (FieldContext fieldContext : fieldContexts) {
            if (fieldContext.hasOption() && !fieldContext.getFieldOption().getExpose()) {
                result.add(fieldContext);
            }
        }
        return result;
    }
}
