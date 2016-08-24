/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;


import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MergeUtils {

    // same as mergeEnumItems, but items is sorted, no copy and maps are LinkedHashMap
    public static <T extends Enum<T>, S extends Enum<S>, K> void
    mergeSortedEnumItems(Map<K, Map<T, Object>> src, Class<T> enumClass, Map<K, Map<S, Object>> items) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        //LinkedHashMap, ordered
        for (Object ent : items.entrySet()) {
            Map.Entry entry = (Map.Entry) ent;
            K id = (K) entry.getKey();
            Map<S, Object> value = (Map<S, Object>) entry.getValue();
            Map<T, Object> data = src.get(id);
            if (data == null) {
                data = new HashMap<T, Object>();
            }
            for (S s : value.keySet()) {
                data.put(Enum.valueOf(enumClass, s.toString()), value.get(s));
            }
            src.put(id, data);
        }
    }

    //src SortedHashMap, items HashMap
    public static <T extends Enum<T>, K, S> void
    mergeSortedEnumItems(Map<K, Map<T, Object>> src, Map<K, S> items, T field) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        for (K id : items.keySet()) {
            Map<T, Object> data = src.get(id);
            if (data == null) {
                data = new HashMap<T, Object>();
            }
            data.put(field, items.get(id));
            src.put(id, data);
        }
    }

    public static <T, K, S> void
    mergeSortedItems(Map<K, Map<T, Object>> src, Map<K, S> items, T field) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        for (K id : items.keySet()) {
            Map<T, Object> data = src.get(id);
            if (data == null) {
                data = new HashMap<T, Object>();
            }
            data.put(field, items.get(id));
            src.put(id, data);
        }
    }

    public static <T extends Enum<T>, K, S> void filterSortedField(Map<K, Map<T, S>> data, T field) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (K id : data.keySet()) {
            Map<T, S> item = data.get(id);
            if (item != null) {
                item.remove(field);
            }
        }
    }


    /**
     * @param <T>
     * @param <S>
     * @param <K>
     * @param src
     * @param enumClass
     * @param items
     * @return
     */
    public static <T extends Enum<T>, S extends Enum<S>, K> Map<K, Map<T, Object>> mergeEnumItems(Map<K, Map<T, Object>> src, Class<T> enumClass, Map<K, Map<S, Object>> items) {
        Map<K, Map<T, Object>> result = new LinkedHashMap<K, Map<T, Object>>(src);
        if (CollectionUtils.isEmpty(items)) {
            return result;
        }
        for (K id : items.keySet()) {
            Map<T, Object> data = src.get(id);
            if (data == null) {
                data = new HashMap<T, Object>();
            }
            Map<S, Object> addData = items.get(id);
            for (S s : addData.keySet()) {
                data.put(Enum.valueOf(enumClass, s.toString()), addData.get(s));
            }
            result.put(id, data);
        }
        return result;
    }

    /**
     * @param <T>
     * @param <S>
     * @param <K>
     * @param src
     * @param items
     * @param field
     * @return
     */
    public static <T extends Enum<T>, K, S> Map<K, Map<T, Object>> mergeEnumItems(Map<K, Map<T, Object>> src, Map<K, S> items, T field) {
        Map<K, Map<T, Object>> result = new HashMap<K, Map<T, Object>>(src);
        if (CollectionUtils.isEmpty(items)) {
            return result;
        }
        for (K id : items.keySet()) {
            Map<T, Object> data = src.get(id);
            if (data == null) {
                data = new HashMap<T, Object>();
            }
            data.put(field, items.get(id));
            result.put(id, data);
        }
        return result;
    }

    /**
     * @param <T>
     * @param <S>
     * @param <K>
     * @param src
     * @param items
     * @return Map<K,T>
     * @author sunkai
     * @date 2012-6-21
     */
    public static <T, S extends Enum<S>, K> Map<K, T> mergeObjectEnumItems(Map<K, T> src, Map<K, Map<S, Object>> items) {
        Map<K, T> result = new LinkedHashMap<K, T>(src);
        if (CollectionUtils.isEmpty(items)) {
            return result;
        }
        for (K id : items.keySet()) {
            T data = src.get(id);
            if (data == null) {
                throw new RuntimeException("data can not be null.");
            }
            Map<S, Object> addData = items.get(id);
            for (S s : addData.keySet()) {
                try {
                    //使用类型转换工具类防止类型转换出错
                    Object value = addData.get(s);
                    PropertyUtils.setProperty(data, s.name(), ConertUtils.convertType(PropertyUtils.getPropertyDescriptor(data,s.name()).getPropertyType(),value));
                } catch (Exception e) {
                    throw new RuntimeException("set property: " + s + " error!", e);
                }
            }
            result.put(id, data);
        }
        return result;
    }

    /**
     * @param <K>
     * @param <T>
     * @param <S>
     * @param src
     * @param data
     * @return
     */
    public static <K, T, S> Map<K, T> mergeObjectItems(Map<K, T> src, Map<K, S> data) {
        Map<K, T> result = new HashMap<K, T>(src);
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (K k : data.keySet()) {
            T row = src.get(k);
            if (row == null) {
                continue;
            }
            BeanUtils.copyProperties(data.get(k), row);
            result.put(k, row);
        }
        return result;
    }

    public static <K, T, S> Map<K, T> mergeObjectItems(Map<K, T> src, Map<K, S> data, String[] ignoreProperties) {
        Map<K, T> result = new HashMap<K, T>(src);
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (K k : data.keySet()) {
            T row = src.get(k);
            if (row == null) {
                continue;
            }
            BeanUtils.copyProperties(data.get(k), row, ignoreProperties);
            result.put(k, row);
        }
        return result;
    }

    /**
     * @param <K>
     * @param <T>
     * @param <S>
     * @param src
     * @param srcProperty
     * @param data
     * @return
     */
    public static <K, T, S> Map<K, T> mergeObjectItems(Map<K, T> src, String srcProperty, Map<K, S> data) {
        Map<K, T> result = new HashMap<K, T>(src);
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (K k : data.keySet()) {
            T row = src.get(k);
            if (row == null) {
                continue;
            }
            try {
                PropertyUtils.setProperty(row, srcProperty, data.get(k));
            } catch (Exception e) {
                throw new RuntimeException("fill propery error", e);
            }
            result.put(k, row);
        }
        return result;
    }

    public static <T extends Enum<T>, K, S> Map<K, Map<T, S>> filterField(Map<K, Map<T, S>> data, T field) {
        Map<K, Map<T, S>> result = new HashMap<K, Map<T, S>>(data);
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (K id : data.keySet()) {
            Map<T, S> item = data.get(id);
            if (item != null) {
                Map<T, S> item4add = new HashMap<T, S>(item);
                item4add.remove(field);
                result.put(id, item4add);
            } else {
                result.put(id, item);
            }
        }
        return result;
    }

    public static <T extends Enum<T>, K, S> Map<K, S> extractData(Map<K, Map<T, S>> data, T field) {
        Map<K, S> result = new HashMap<K, S>();
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (K id : data.keySet()) {
            Map<T, S> item = data.get(id);
            if (item != null && item.get(field) != null) {
                result.put(id, item.get(field));
            }
        }
        return result;

    }

    /**
     * null will be ignored
     *
     * @param <K>
     * @param <T>
     * @param <S>
     * @param src
     * @param rowPrc
     * @return
     */
    public static <K, T, S> Map<K, T> filterData(Map<K, S> src, RowProcessor<S, T> rowPrc) {
        Map<K, T> result = new HashMap<K, T>();
        if (CollectionUtils.isEmpty(src)) {
            return result;
        }
        for (K k : src.keySet()) {
            S s = src.get(k);
            if (s == null) {
                continue;
            }
            T t = rowPrc.process(s);
            if (t == null) {
                continue;
            }
            result.put(k, t);
        }
        return result;

    }

    public static interface RowProcessor<S, T> {
        public T process(S s);
    }

}
