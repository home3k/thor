/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.dal.columnrowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.haoyayi.thor.dal.bo.${Classname};
import com.haoyayi.thor.dal.meta.${Classname}Column;

import org.springframework.jdbc.core.RowMapper;

/**
* This class is automatically generated, Unless special scene, DO NOT modify!!
*/
public class ${Classname}ColumnRowMapper implements RowMapper<Map<${Classname}Column,Object>> {
	
	private Set<${Classname}Column> columns;
	
	public ${Classname}ColumnRowMapper(Set<${Classname}Column> columns){
		this.columns=columns;
	}

	public Map<${Classname}Column,Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<${Classname}Column,Object> items = new HashMap<${Classname}Column,Object>();
		${setStr}
        return items;
	}
}
