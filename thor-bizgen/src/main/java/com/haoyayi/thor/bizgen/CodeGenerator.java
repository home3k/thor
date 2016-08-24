/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen;

import com.haoyayi.thor.bizgen.meta.ModelContext;

import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface CodeGenerator {

    void generateCode(ModelContext context, Set<String> actions) throws Exception;

}
