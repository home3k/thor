/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.context.meta;

import java.util.Map;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CodegenProcessorContext {

    private Map<String, String> processors;

    public Map<String, String> getProcessors() {
        return processors;
    }

    public void setProcessors(Map<String, String> processors) {
        this.processors = processors;
    }
}
