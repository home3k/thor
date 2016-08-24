/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.dal.base;

import java.lang.reflect.Field;
import java.util.*;

public class FieldQualifierUtils {
	
	/**
     *
	 */
	private static Map<Class,Map<String,String>> qualifierMap = new HashMap<Class, Map<String,String>>();
	
	/**
     *
	 */
	private static Map<Class,Map<String,String>> qualifierRevertMap = new HashMap<Class, Map<String,String>>();
	
	
	private static  Map<String,String> getQualifierMap(Class clazz){
		if(qualifierMap.containsKey(clazz)){
			return qualifierMap.get(clazz);
		}
		Map<String,String> common2qualifier = new HashMap<String, String>();
		Map<String,String> qualifier2common = new HashMap<String, String>();
		for(Field field:clazz.getFields()){
			com.haoyayi.thor.dal.base.FieldQualifier anno = field.getAnnotation(com.haoyayi.thor.dal.base.FieldQualifier.class);
			if(anno!=null){
				common2qualifier.put(field.getName(), anno.value());
				qualifier2common.put(anno.value(), field.getName());
			}
		}
		qualifierMap.put(clazz, common2qualifier);
		qualifierRevertMap.put(clazz, qualifier2common);
		return common2qualifier;
	}
	
	private static Map<String,String> getQualifierRevertMap(Class clazz){
		if(qualifierRevertMap.containsKey(clazz)){
			return qualifierRevertMap.get(clazz);
		}
		getQualifierMap(clazz);
		return qualifierRevertMap.get(clazz);
	}
	
	/**
	 * @param columnEnum
	 * @return
	 */
	public static String getDBColumnName(Object columnEnum){
		Map<String, String> commonname2qualifier=getQualifierMap(columnEnum.getClass());
		if(commonname2qualifier!=null){
			String dbColumnName = commonname2qualifier.get(columnEnum.toString());
			if(dbColumnName!=null){
				return "`" + dbColumnName + "`";
			}
		}
		return "`" + columnEnum.toString() + "`";
	}
	
	/**
	 * @param clazz
	 * @param dbColumnName
	 * @return
	 */
	public static String getCommonColumnName(Class clazz,String dbColumnName){
		Map<String,String> qualifier2commonname=getQualifierRevertMap(clazz);
		if(qualifier2commonname!=null){
			String commonname = qualifier2commonname.get(dbColumnName);
			if(commonname!=null){
				return commonname;
			}
		}
		return dbColumnName;
	}
	
	public static Set<String> getDBColumns(Set commonColumns){
		Set<String> result  = new HashSet<String>();
		if(commonColumns==null||commonColumns.isEmpty()){
			return result;
		}
		for(Object commonColumn:commonColumns){
			result.add(getDBColumnName(commonColumn));
		}
		return result;
	}

	public static Set<String> getDBColumnsAsColumn(Set commonColumns){
		Set<String> result  = new HashSet<String>();
		if(commonColumns==null||commonColumns.isEmpty()){
			return result;
		}
		for(Object commonColumn:commonColumns){
			result.add(getDBColumnName(commonColumn) + " as " + commonColumn);
		}
		return result;
	}

    public static String getDBColumns(Object commonColumn){
        if(commonColumn==null){
            return "";
        }
        return getDBColumnName(commonColumn);
    }
	
	/**
	 * @param commonColumns
	 * @return
	 */
	public static List<String> getDBColumns(List commonColumns){
		List<String> result  = new ArrayList<String>();
		if(commonColumns==null||commonColumns.isEmpty()){
			return result;
		}
		for(Object commonColumn:commonColumns){
			result.add(getDBColumnName(commonColumn));
		}
		return result;
	}

}
