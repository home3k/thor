/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.load;

import java.util.Map;

import com.google.common.collect.Maps;
import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CamelUtils;
import com.haoyayi.thor.bizgen.GenType;
import com.haoyayi.thor.bizgen.context.CodegenContextHolder;
import com.haoyayi.thor.bizgen.meta.ModelContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.yaml.snakeyaml.Yaml;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class YamlContextLoader implements ContextLoader {

    private ApplicationContext context;
    static final Log LOG = LogFactory.getLog(YamlContextLoader.class);

    public ModelContext load() {
        return load("book", true);
    }

    public ModelContext load(boolean refresh) {
        return load("book", refresh);
    }

    public ModelContext load(String model) {
        return load(model, true);
    }

    public ModelContext load(String model, boolean refresh) {
        String prefix = "model";
        if (model.startsWith("dict")) {
            prefix = "dict";
            model = "dict";
        }
        String path = prefix + "/" + CamelUtils.upperFirst(model) + ".yaml";
        try {

            Yaml yaml = new Yaml();
            LOG.info("Loading the model with path: " + path +"...");
            ModelContext modelContext = yaml.loadAs(this.getClass().getClassLoader().getResourceAsStream(path), ModelContext.class);
            if (modelContext.getDict() == null) {
                if (refresh) {
                    modelContext.refresh(context);
                }
            } else {
                for (ModelContext m : modelContext.getDict()) {
                    if (refresh) {
                        m.refresh(context);
                    }
                }
            }
            return modelContext;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
