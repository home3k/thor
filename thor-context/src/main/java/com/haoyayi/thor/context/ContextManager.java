/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context;

import com.haoyayi.thor.context.loader.YamlContextLoader;
import com.haoyayi.thor.context.meta.ModelContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */

@Service
public class ContextManager implements ApplicationContextAware {

    ApplicationContext applicationContext;

    YamlContextLoader contextLoader;

    Map<String, ModelContext> contextMap = new HashMap<String, ModelContext>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        contextLoader = new YamlContextLoader();
        contextLoader.setApplicationContext(applicationContext);
    }

    public ModelContext getDictContext(String model) {
        return getDictContext(model, true);
    }

    public ModelContext getDictContext(String model, boolean refresh) {
        if (contextMap.containsKey(model)) {
            return contextMap.get(model);
        } else {
            ModelContext context = contextLoader.load("dict", refresh);
            for (ModelContext dictContext : context.getDict()) {
                contextMap.put(dictContext.getName(), dictContext);
            }
            return contextMap.get(model);
        }
    }

    public ModelContext getContext(String model) {
        return getContext(model, true);
    }
    public ModelContext getContext(String model, boolean refresh) {
        if (contextMap.containsKey(model)) {
            return contextMap.get(model);
        } else {
            ModelContext context = contextLoader.load(model, refresh);
            if (refresh) {
                contextMap.put(model, context);
            }
            return context;
        }
    }
}
