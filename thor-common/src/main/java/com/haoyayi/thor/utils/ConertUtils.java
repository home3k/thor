/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.AbstractConverter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author home3k
 */
public class ConertUtils {

   static  {
        ConvertUtils.register(new DateConvert(), Date.class);
        ConvertUtils.register(new ArrayConvert(), Object[].class);
    }
    /**
     * 转换类型
     *
     * @param
     * @return
     */
    public static <T, V> void convertType(T bo, V bto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Map<String, Object> bmap = PropertyUtils.describe(bto);
        bmap.remove("class");
        for (PropertyDescriptor item : org.springframework.beans.BeanUtils.getPropertyDescriptors(bo.getClass())) {
            if (!bmap.containsKey(item.getName()))
                continue;
            Object value = bmap.get(item.getName());
            Class type = item.getPropertyType();
            if (value != null) {
                Converter converter = ConvertUtils.lookup(type);
                Object result = converter.convert(item.getPropertyType(), value);
                PropertyUtils.setProperty(bo, item.getName(), result);
            }
        }
    }

    /**
     * 转换类型
     *
     * @param value
     * @return
     */
    public static Object convertType(Class destType, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (value==null)
            return  null;
        ConvertUtils.register(new DateConvert(), Date.class);
        ConvertUtils.register(new ArrayConvert(), Map[].class);
        Converter converter = ConvertUtils.lookup(destType);
        if (converter == null) {
            return value;
        }
        return converter.convert(destType, value);
    }

    /**
     * 转换类型
     *
     * @param context
     * @return
     */
    public static <V> Map<V, Object> convertType(Map<V, Object> context, Map<String, Class> pdmap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<V, Object> result = new HashMap<V, Object>();
        for (V item : context.keySet()) {
            Method method = item.getClass().getMethod("name");
            String name = (String) method.invoke(item);
            if (pdmap.get(name) != null) {
                Converter converter = ConvertUtils.lookup(pdmap.get(name));
                result.put(item, converter.convert(pdmap.get(name), context.get(item)));
            }
        }
        return result;
    }


    static class DateConvert extends AbstractConverter {

        private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        protected Object convertToType(Class aClass, Object value) throws Throwable {
            if (value instanceof String)
                try {
                    return timeFormat.parse((String) value);
                } catch (Exception e) {
                    return dateFormat.parse((String) value);
                }
            return value;
        }

        @Override
        protected Class getDefaultType() {
            return Date.class;
        }
    }
    
    static class ArrayConvert implements Converter {

		@Override
		public Object convert(Class type, Object value) {
			Class<?> arrayClass = null;
			if (type.isAssignableFrom(Map[].class)) {
				arrayClass = Map.class;
			}
			Object[] result = (Object[]) Array.newInstance(arrayClass, ((Collection<?>) value).size());
			Iterator<?> iterator = ((Collection<?>) value).iterator();
			for (int i = 0; iterator.hasNext(); i++) {
				result[i] = iterator.next();
			}
			return result;
		}

    }
}
