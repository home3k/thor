/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.command;

import com.google.common.base.Splitter;
import com.haoyayi.thor.bizgen.CamelUtils;

import java.io.File;
import java.util.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class Commander {

    private String path = "model";

    private static final String TOKEN = "*";
    private static final String ALL_TOKEN = "all";
    private static final String SPE_TOKEN = ".";
    private static final String ADDFIELD_TOKEN = "addfield";


    private String fetchModel(String file) {
        if (!file.contains(SPE_TOKEN)) {
            return "";
        } else {
            List<String> items = Splitter.on(SPE_TOKEN).trimResults().splitToList(file);
            return items.get(0);
        }
    }

    private List<String> loadModels() {
        List<String> models = new ArrayList<String>();
        File modelDir = new File(this.getClass().getClassLoader().getResource(path).getFile());
        for (File file : modelDir.listFiles()) {
            models.add(fetchModel(CamelUtils.lowerFirst(file.getName())));
        }
        return models;
    }


    public CommandItem command(String[] args) {

	    	Map<String, Set<String>> procs = CommandContext.procs;
        Map<String, Set<String>> addField = CommandContext.addField;
        CommandItem item = new CommandItem();
        if (args == null || args.length != 2) {
            System.out.println("Usage: Base model step");
            return null;
        }
        String model = args[0];
        String step = args[1];
        List<String> models = new ArrayList<String>();
        Map<String, Set<String>> steps = new HashMap<String, Set<String>>();
        if (ALL_TOKEN.equals(model)) {
            models.addAll(loadModels());
        } else {
            models.add(model);
        }

        if(ADDFIELD_TOKEN.equalsIgnoreCase(step)) {
        		steps.putAll(addField);
        } else if (ALL_TOKEN.equals(step)) {
            steps.putAll(procs);
        } else {
            if (step.contains(SPE_TOKEN)) {
                List<String> items = Splitter.on(SPE_TOKEN).trimResults().splitToList(step);
                String key = items.get(0);
                String proc = items.get(1);
                if (TOKEN.equals(proc)) {
                    if (!procs.containsKey(key)) {
                        throw new IllegalArgumentException("The params invalid");
                    }
                    steps.put(key, procs.get(key));
                } else {
                    Set<String> set = new HashSet<String>();
                    set.add(proc);
                    steps.put(key, set);
                }

            } else {
                if (procs.containsKey(step)) {
                    steps.put(step, procs.get(step));
                } else {
                    throw new IllegalArgumentException("The params invalid");
                }
            }
        }

        item.setModel(models);
        item.setStep(steps);

        return item;
    }

}
