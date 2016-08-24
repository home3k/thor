/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CommandItem {

    private List<String> model;

    private Map<String, Set<String>> step;

    public List<String> getModel() {
        return model;
    }

    public void setModel(List<String> model) {
        this.model = model;
    }

    public Map<String, Set<String>> getStep() {
        return step;
    }

    public void setStep(Map<String, Set<String>> step) {
        this.step = step;
    }

}