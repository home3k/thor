/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class CommandContext {

	public static Map<String, Set<String>> procs = new HashMap<String, Set<String>>();
    public static Map<String, Set<String>> addField = new HashMap<String, Set<String>>();

    public Map<String, Set<String>> getProcs() {
        return procs;
    }

    public void setProcs(Map<String, Set<String>> procs) {
        CommandContext.procs = procs;
    }

	public Map<String, Set<String>> getAddField() {
		return addField;
	}

	public void setAddField(Map<String, Set<String>> addField) {
		CommandContext.addField = addField;
	}
}
