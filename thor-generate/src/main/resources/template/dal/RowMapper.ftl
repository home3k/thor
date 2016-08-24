/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.dal.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.haoyayi.thor.dal.bo.${Classname};

import org.springframework.jdbc.core.RowMapper;

/**
* This class is automatically generated, Unless special scene, DO NOT modify!!
*/
public class ${Classname}RowMapper implements RowMapper<${Classname}> {

     public ${Classname} mapRow(ResultSet rs, int rowNum) throws SQLException {
         ${Classname} ${classname}  = new ${Classname}();
         ${setStr}
         return ${classname};
     }

}
