/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen;

import com.haoyayi.thor.bizgen.command.CommandItem;
import com.haoyayi.thor.bizgen.command.Commander;
import com.haoyayi.thor.bizgen.context.CodegenContextHolder;
import com.haoyayi.thor.bizgen.load.ContextLoader;
import com.haoyayi.thor.bizgen.load.YamlContextLoader;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import com.haoyayi.thor.bizgen.processor.ModelProcessor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sh codegen.sh model all
 * sh codegen.sh model api.*
 * sh codegen.sh dict.all all
 * sh codegen.sh dict.testdict api.*
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CodegenRunner {

    public static void main(String[] args) throws Exception {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:ctx-gendb.xml", "classpath:ctx-gendb-desc.xml",
                "classpath:ctx-gen.xml", "classpath:ctx-meta.xml", "classpath:ctx-codegen.xml"});
        CommandItem commandItem = new Commander().command(args);

        List<String> models = commandItem.getModel();

        Map<String, Set<String>> procs = commandItem.getStep();

        try {
            ContextLoader contextLoader = new YamlContextLoader();
            contextLoader.setApplicationContext(applicationContext);
            ModelProcessor modelProcessor = applicationContext.getBean("modelProcessor", ModelProcessor.class);
            modelProcessor.setApplicationContext(applicationContext);

            for (String model : models) {

                ModelContext modelContext = contextLoader.load(model);
                if (model.startsWith("dict")) {
                    CodegenContextHolder.getInstance().setCodegenContext("genType", GenType.dict);
                    modelContext.setDictModel(getSubModel(model));
                } else {
                    CodegenContextHolder.getInstance().setCodegenContext("genType", GenType.model);
                }
                modelProcessor.process(modelContext, procs);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            CodegenContextHolder.getInstance().clear();
        }

    }

    private static String getSubModel(String model) {
        return model.substring(model.indexOf(".") + 1, model.length());
    }

}
