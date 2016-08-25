/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.context.loader;

import com.haoyayi.thor.context.meta.ModelContext;
import org.springframework.context.ApplicationContext;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public interface ContextLoader {

    void setApplicationContext(ApplicationContext context);

    ModelContext load();

    ModelContext load(String model);

    ModelContext load(boolean refresh);

    ModelContext load(String model, boolean refresh);

}
