/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.bizgen.dml;

import com.haoyayi.thor.bizgen.AbstractCodegenerator;
import com.haoyayi.thor.bizgen.CodeGenerator;
import com.haoyayi.thor.bizgen.meta.ModelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Set;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Service
public class DatabaseManager extends AbstractCodegenerator implements CodeGenerator {

    @Override
    public void generateCode(ModelContext context, Set<String> actions) throws Exception {

    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    protected String getCodegenBasePath() {
        return "";
    }

    public void _generateCode() throws Exception {
        generateCode("thor", datasourcethor);
    }

    private void generateCode(String db, DataSource datasource) throws Exception {

        jdbcTemplate.execute("");


    }

}
