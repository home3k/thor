/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.processor;

import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.GenType;
import com.haoyayi.thor.bizgen.context.CodegenContextHolder;
import com.haoyayi.thor.bizgen.meta.CodegenContext;
import com.haoyayi.thor.bizgen.meta.CodegenProcessorContext;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class ModelProcessor {

    protected GenType getGenType() {
        Object object = CodegenContextHolder.getInstance().getCodegenContext("genType");
        if (object == null ) {
            return GenType.model;
        } else {
            return (GenType)object;
        }
    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void process(ModelContext context,Map<String, Set<String>> procs) {
        try {
            CodegenProcessorContext codegenProcessorContext = (CodegenProcessorContext) applicationContext.getBean("codegenProcessorContext");
            for (String procStr : procs.keySet()) {
                String processorStr = codegenProcessorContext.getProcessors().get(procStr);
                CodeGenerator processor = applicationContext.getBean(processorStr, CodeGenerator.class);

                processor.generateCode(context, procs.get(procStr));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
