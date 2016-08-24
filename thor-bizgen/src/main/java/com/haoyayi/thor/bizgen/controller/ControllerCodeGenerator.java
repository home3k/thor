/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.GenType;
import com.haoyayi.thor.bizgen.meta.ModelContext;

import freemarker.template.TemplateException;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ControllerCodeGenerator extends AbstractCodegenerator implements CodeGenerator {

    private String base = "controller";

    private static final String ACTION_API = "api";

    @Override
    protected String getCodegenBasePath() {
        return "thor-server/src/main/java/com/haoyayi/thor/server/controller";
    }

    @Override
    public void generateCode(ModelContext context, Set<String> actions) throws Exception {
        if (actions.contains(ACTION_API)) {
            this.generateApi(context);
        }
    }

    private void generateApi(ModelContext context) throws IOException, TemplateException {

        Map<String, Object> data = new HashMap<String, Object>();
        if (getGenType()== GenType.dict) {
            List<Map<String, Object>> models = new ArrayList<Map<String, Object>>();
            for (ModelContext dicContext : context.getDict()) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("model", dicContext.getName());
                model.put("desc", dicContext.getDesc());
                model.put("Model", CamelUtils.upperFirst(dicContext.getName()));
                models.add(model);
            }
            data.put("models", models);
            generateFile(base, getFtlPathPrefix() + "controller/" + "Controller.ftl", "DictController.java", data, "");

        } else {
            data.put("model", context.getName());
            data.put("desc", context.getDesc());
            data.put("Model", CamelUtils.upperFirst(context.getName()));
            generateFile(base, getFtlPathPrefix() + "controller/" + "Controller.ftl", CamelUtils.upperFirst(context.getName()) + "Controller.java", data, "");

        }

    }

}

