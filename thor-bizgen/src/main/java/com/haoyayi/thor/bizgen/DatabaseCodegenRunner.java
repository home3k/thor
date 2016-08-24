/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen;

import com.haoyayi.thor.bizgen.dal.DaoGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class DatabaseCodegenRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }
        String[] tables = args;
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:ctx-gendb.xml", "classpath:ctx-gendb-desc.xml",
                "classpath:ctx-gen.xml", "classpath:ctx-meta.xml", "classpath:ctx-codegen.xml"});

        DaoGenerator daoGenerator = applicationContext.getBean("daoGenerator", DaoGenerator.class);
        try {
            daoGenerator.generateCode(null, new HashSet<String>(Arrays.asList(tables)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
