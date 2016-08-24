/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class EnumUtils {


    /**
     * 从枚举map中过滤中于指定枚举同名的记录，并且将key转换为指定的枚举对象
     *
     * @param <T>
     * @param <S>
     * @param items
     * @param enumClass
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>> Map<S, Object> filterAndConvertEnumItemMap(Map<T, Object> items, Class<S> enumClass) {
        Map<S, Object> result = new HashMap<S, Object>();
        if (null == items) {
            return result;
        }
        for (S filter : enumClass.getEnumConstants()) {
            for (T key : items.keySet()) {
                if (filter.toString().equals(key.toString())) {
                    result.put(filter, items.get(key));
                }
            }
        }
        return result;
    }

    /**
     * 从枚举map value中过滤中于指定枚举同名的记录，并且将value转换为指定的枚举对象
     *
     * @param items
     * @param enumClass
     * @param <X>
     * @param <T>
     * @param <S>
     * @return
     */
    public static <X, T extends Enum<T>, S extends Enum<S>> Map<X, S> filterAndConvertEnumItemMapValue(Map<X, T> items, Class<S> enumClass) {
        Map<X, S> result = new HashMap<X, S>();
        if (null == items) {
            return result;
        }
        for (S filter : enumClass.getEnumConstants()) {
            for (X key : items.keySet()) {
                T value = items.get(key);
                if (filter.toString().equals(value.toString())) {
                    result.put(key, filter);
                }
            }
        }
        return result;
    }

    /**
     * 从枚举set中过滤中于指定枚举同名的记录，并且将key转换为指定的枚举对象
     *
     * @param items
     * @param enumClass
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>> Set<S> filterAndConvertEnumItemSet(Set<T> items, Class<S> enumClass) {
        Set<S> result = new HashSet<S>();
        if (null == items) {
            return result;
        }
        for (S filter : enumClass.getEnumConstants()) {
            for (T key : items) {
                if (filter.toString().equals(key.toString())) {
                    result.add(filter);
                }
            }
        }
        return result;
    }


    /**
     * 从枚举map中过滤中于指定枚举集合的记录，并且将key转换为指定的枚举对象
     *
     * @param <T>
     * @param <S>
     * @param items
     * @param enumSet
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>> Map<S, Object> filterAndConvertEnumItemMap(Map<T, Object> items, Set<S> enumSet) {
        Map<S, Object> result = new HashMap<S, Object>();
        for (S filter : enumSet) {
            for (T key : items.keySet()) {
                if (filter.toString().equals(key.toString())) {
                    result.put(filter, items.get(key));
                }
            }
        }
        return result;
    }


    /**
     * 从枚举set中过滤中于指定枚举同名的记录，并且将key转换为指定的枚举对象
     *
     * @param <T>
     * @param <S>
     * @param fields
     * @param enumClass
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>> Set<S> filterAndConvertEnumItem(Set<T> fields, Class<S> enumClass) {
        Set<S> result = new HashSet<S>();
        for (S filter : enumClass.getEnumConstants()) {
            for (T key : fields) {
                if (filter.toString().equals(key.toString())) {
                    result.add(filter);
                }
            }
        }
        return result;
    }

    public static <T extends Enum<T>, S extends Enum<S>> Set<S> filterAndConvertEnumItem(Set<T> fields, Collection<S> enumCollection) {
        Set<S> result = new HashSet<S>();
        for (S filter : enumCollection) {
            for (T key : fields) {
                if (filter.toString().equals(key.toString())) {
                    result.add(filter);
                }
            }
        }
        return result;
    }


    /**
     * 将枚举集合中给定class的同名枚举对象过滤掉
     *
     * @param <T>
     * @param <S>
     * @param srcEnumset
     * @param subEnum
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>> Set<T> subEnum(Set<T> srcEnumset, Class<S> subEnum) {
        Set<T> result = new HashSet<T>();
        if (CollectionUtils.isEmpty(srcEnumset)) {
            return result;
        }
        for (T t : srcEnumset) {
            boolean contains = false;
            for (S s : subEnum.getEnumConstants()) {
                if (t.toString().equals(s.toString())) {
                    contains = true;
                    continue;
                }
            }
            if (!contains) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 将制定BO，根据枚举集合，拆分成 enum -> fieldobject的 Map 形式
     *
     * @param <T>
     * @param <S>
     * @param item
     * @param enumFields
     * @return
     */
    public static <T, S extends Enum<S>> Map<S, Object> filterAndConvertEnumItemMap(T item, S[] enumFields) {
        Map<S, Object> result = new HashMap<S, Object>();
        if (null == item) {
            return result;
        }
        try {
            for (S filter : enumFields) {
                Object fieldValue = PropertyUtils.getProperty(item, filter.toString());
                result.put(filter, fieldValue);
            }
        } catch (Exception e) {
            throw new IllegalStateException("filter", e);
        }

        return result;
    }

    /**
     * 把field Y 转换称 field X
     *
     * @param fields
     * @param subEnum
     * @param <X>
     * @param <Y>
     * @return
     */
    public static <X extends Enum<X>, Y extends Enum<Y>> Set<X> getFields(Set<Y> fields, Class<X> subEnum) {
        Set<X> result = new HashSet<X>();
        for (Y field : fields) {
            result.add(X.valueOf(subEnum, field.name()));
        }
        return result;
    }

    /**
     * 转换同时将value转换指定对象属性类型
     *
     * @param items
     * @param enumClass
     * @param obj
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>> Map<S, Object> filterAndConvertEnumItemMap(Map<T, Object> items, Class<S> enumClass, Object obj) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<S, Object> result = new HashMap<S, Object>();
        if (null == items) {
            return result;
        }
        for (S filter : enumClass.getEnumConstants()) {
            for (T key : items.keySet()) {
                if (filter.toString().equals(key.toString())) {
                    result.put(filter, ConertUtils.convertType(PropertyUtils.getPropertyType(obj, key.toString()), items.get(key)));
                }
            }
        }
        return result;
    }


}

