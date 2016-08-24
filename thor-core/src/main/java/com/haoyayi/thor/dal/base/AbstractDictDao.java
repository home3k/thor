/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.dal.base;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.haoyayi.thor.cache.impl.CacheProxy;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public abstract class AbstractDictDao<T extends AbstractBo, C> extends AbstractDAO<T, C> {

	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractDictDao.class);
    protected DictCache dictCache = new LocalDictCache();
    
    @Override
    public void afterPropertiesSet() throws Exception {
    	if ("redis".equals(System.getProperty("dictCache"))) {
    		dictCache = new RedisDictCache();
    	}
    	LOGGER.info("dict cache with [" + dictCache.getClass() + "]");
    	initCache();
    }

    private void initCache() {
    	dictCache.init();
    }
    
    private String getDictCacheKey(Long id) {
    	return getTablename() + "-" + id;
    }
    
    /**
     * 获得字段字段内容
     *
     * @return
     */
    protected abstract C getDictColumn();

    private Map<Long, String> getDictFromCache(Collection<Long> ids) {
    	Map<Long, String> result = new HashMap<Long, String>();
    	List<String> dictValues = dictCache.get(ids);
    	int index = 0;
    	for (Long id : ids) {
    		result.put(id, dictValues.get(index++));
    	}
    	return result;
    }


    public Map<Long, String[]> getDict(Map<Long, Object> model4dictInfos, DictStoreType storeType) {

        Map<Long, String[]> result = new HashMap<Long, String[]>();
        Set<Long> allQueryIds = Sets.newHashSet();
        for (Object obj : model4dictInfos.values()) {
        	if (storeType == DictStoreType.joiner) {
        		List<String> strIds = Splitter.on(",").trimResults().splitToList((String) obj);
        		for (String strId : strIds) {
        			allQueryIds.add(Long.parseLong(strId));
        		}
        	} else if (storeType == DictStoreType.mask) {
        		String longId = (String) obj;
        		BigInteger mask = new BigInteger("1");
//        		long mask = 1;
                for (long i = 1; i <= 130; i++) {
                    if (i > 1) {
                    	mask = mask.shiftLeft(1);
//                        mask = mask << 1;
                    }
                    if (mask.and(new BigInteger(longId)).equals(mask)) {
//                    if ((longId & mask) == mask) {
                    	allQueryIds.add(i);
                    }
                }
        	}
        }
        Map<Long, String> resultItem = getDictFromCache(allQueryIds);
        for (Long modelid : model4dictInfos.keySet()) {
        	Object obj = model4dictInfos.get(modelid);
        	if (storeType == DictStoreType.joiner) {
        		List<String> strIds = Splitter.on(",").trimResults().splitToList((String) obj);
        		List<String> dicts = Lists.newArrayList();
        		for (String strId : strIds) {
        			dicts.add(resultItem.get(Long.parseLong(strId)));
        		}
        		result.put(modelid, dicts.toArray(new String[0]));
        	} else if (storeType == DictStoreType.mask) {
        		String longId = (String) obj;
//        		long mask = 1;
        		BigInteger mask = new BigInteger("1");
        		List<String> dicts = Lists.newArrayList();
                for (long i = 1; i <= 130; i++) {
                    if (i > 1) {
                    	mask = mask.shiftLeft(1);
//                        mask = mask << 1;
                    }
                    if (mask.and(new BigInteger(longId)).equals(mask)) {
//                    if ((longId & mask) == mask) {
                    	dicts.add(resultItem.get(i));
                    }
                }
                result.put(modelid, dicts.toArray(new String[0]));
        	}
        }
        
        return result;
    }

    
    /**
     * @param model4dictId
     * @return
     */
    public Map<Long, String> getDict(Map<Long, Long> model4dictId) {

        Map<Long, String> id4dict = new LinkedHashMap<Long, String>();

        Map<C, Object> conditions = new HashMap<C, Object>();
        Set<Long> ids2Get = new TreeSet<Long>(model4dictId.values());
        
        Map<Long, String> dicts = getDictFromCache(ids2Get);

//        Map<Long, String> data = getDictFromCache(ids2Get);

        for (Long id : model4dictId.keySet()) {
        	String dict = dicts.get(model4dictId.get(id));
//            String dict = data.get(model4dictId.get(id));
            if (dict == null) {
                continue;
            }
            id4dict.put(id, dict);
        }
        ids2Get.removeAll(dicts.keySet());

        if (ids2Get.isEmpty()) {
            return id4dict;
        }

        conditions.put(getPk(), ids2Get);
        Set<C> fields = new HashSet<C>();
        fields.add(getPk());
        fields.add(getDictColumn());
        Map<Long, Map<C, Object>> items = getItemsByCondition(conditions, fields);
        for (Long itemId : items.keySet()) {
        	if(items.get(itemId).get(getDictColumn()) != null) {
        		dictCache.set(itemId, items.get(itemId).get(getDictColumn()).toString());
        	}
//        	CacheProxy.hset(DICT_CACHE, getDictCacheKey(itemId), (String) items.get(itemId).get(getDictColumn()));
//            dictCache.put(itemId, (String) items.get(itemId).get(getDictColumn()));
        }
        for (Long id : model4dictId.keySet()) {
            if (items.get(model4dictId.get(id))==null) {
                continue;
            }
            if( items.get(model4dictId.get(id)).get(getDictColumn()) != null) {
	            	String dict =  items.get(model4dictId.get(id)).get(getDictColumn()).toString();
	            	id4dict.put(id, dict);
            }
        }
        return id4dict;
    }

    protected Map<Long, Map<C, Object>> getItemsByCondition(Map<C, Object> conditions, Set<C> fields,
                                                            List<Pair<C, Boolean>> orderbys, Integer num, Integer offset) {
        Map<Long, Map<C, Object>> result = new LinkedHashMap<Long, Map<C, Object>>();

        Set<C> fields4Query = new HashSet<C>(fields);
        boolean fillPkFiled = false;
        if (!fields4Query.contains(getPk())) {
            fillPkFiled = true;
            fields.add(getPk());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(StringUtils.join(FieldQualifierUtils.getDBColumns(fields), ","));
        sb.append(" from ");
        sb.append(getTablename());
        List<String> param = new ArrayList<String>();
        if (conditions != null && conditions.size() > 0) {
            sb.append(buildWhereSql(conditions, param));
        }
        if (orderbys != null) {
            //order by:
            sb.append(" order by ");
            boolean first = false;
            for (Pair<C, Boolean> pair : orderbys) {
                if (first) {
                    sb.append(", ");
                } else {
                    first = true;
                }
                sb.append(FieldQualifierUtils.getDBColumnName(pair.getKey()));
                sb.append(" ");
                sb.append(pair.getValue() ? " desc " : " asc ");
            }
        } else {
            sb.append(" order by ");
            sb.append(FieldQualifierUtils.getDBColumnName(getPk()));
            sb.append(" desc ");
        }
        //limit
        if (offset != null) {
            sb.append(" limit ").append(offset);
        }
        if (num != null) {
            if (offset != null) {
                sb.append(" , ").append(num);
            } else {
                sb.append(" limit ").append(num);
            }
        }
        sb.append(";");
        List<Map<C, Object>> data = getJdbcTemplate().query(sb.toString(), getRowMapper(fields), param.toArray());

        if (CollectionUtils.isEmpty(data)) {
            return result;
        }
        for (Map<C, Object> row : data) {
            Long key = (Long) row.get(getPk());
            if (fillPkFiled) {
                row.remove(getPk());
            }
            result.put(key, row);
        }
        return result;
    }
    
    private interface DictCache {
    	void init();
    	String get(Long key);
    	List<String> get(Collection<Long> key);
    	void set(Long key, String value);
    }
    
    private class RedisDictCache implements DictCache {
    	protected String DICT_CACHE_KEY_INIT = getTablename() + "";
        protected String DICT_CACHE = "dictCache";
		@Override
		public String get(Long key) {
			return CacheProxy.hget(DICT_CACHE, getDictCacheKey(key));
		}
		@Override
		public void init() {
//			if ("true".equals(CacheProxy.getAsString(DICT_CACHE_KEY_INIT))) {
//	    		return;
//	    	}
//	    	try {
    		CacheProxy.set(DICT_CACHE_KEY_INIT, "true", -1);
    		Set<C> fields = new HashSet<C>(Arrays.asList(getDictColumn(), getPk()));
    		Map<Long, Map<C, Object>> dicts = getItemsByCondition(new HashMap<C, Object>(), fields, null, null, null);
    		Map<String, String> values = Maps.newHashMap();
    		for (Long id : dicts.keySet()) {
    			if(dicts.get(id).get(getDictColumn()) != null) {
    				values.put(getDictCacheKey(id), dicts.get(id).get(getDictColumn()).toString());
    			}
    		}
    		if (MapUtils.isNotEmpty(values)) {
    			CacheProxy.hmset(DICT_CACHE, values);
    		}
//	    	} finally {
//	    		CacheProxy.del(DICT_CACHE_KEY_INIT);
//	    	}			
		}
		@Override
		public void set(Long key, String value) {
			CacheProxy.hset(DICT_CACHE, getDictCacheKey(key), value);			
		}
		@Override
		public List<String> get(Collection<Long> key) {
			List<String> keys = Lists.newArrayList();
			for (Long k : key) {
				if (k != null) {
					keys.add(getDictCacheKey(k));
				}
			}
			if (org.apache.commons.collections.CollectionUtils.isNotEmpty(keys)) {
				return CacheProxy.hmget(DICT_CACHE, keys.toArray(new String[0]));
			}
			return Lists.newArrayList();
		}
    	
    }
    
    private class LocalDictCache implements DictCache {

    	protected Map<Long, String> dictCache = new ConcurrentHashMap<Long, String>();
		@Override
		public String get(Long key) {
			return dictCache.get(key);
		}
		@Override
		public List<String> get(Collection<Long> key) {
			List<String> result = Lists.newArrayList();
			for (Long k : key) {
				result.add(get(k));
			}
			return result;
		}
		@Override
		public void init() {
			dictCache.clear();
	        Set<C> fields = new HashSet<C>(Arrays.asList(getDictColumn(), getPk()));
	        Map<Long, Map<C, Object>> dicts = getItemsByCondition(new HashMap<C, Object>(), fields, null, null, null);
	        for (Long id : dicts.keySet()) {
	        		Object value = dicts.get(id).get(getDictColumn());
	        		if(value != null) {
	        			dictCache.put(id, value.toString());
	        		}
	        }
		}
		@Override
		public void set(Long key, String value) {
			dictCache.put(key, value);			
		}
    	
    }

    @Override
    protected List<Long> add(List<T> bos, boolean autoKey) {
        List<Long> result = super.add(bos, autoKey);
        initCache();
        return result;
    }

    @Override
    protected List<Long> add(List<T> bos) {
        List<Long> result = super.add(bos);
        initCache();
        return result;
    }

    @Override
    protected void del(Map<C, Object> conditions) {
        super.del(conditions);
        initCache();
    }

    @Override
    public List<Long> addBatchWithAutoKey(List<T> bos) {
        List<Long> result = super.addBatchWithAutoKey(bos);
        initCache();
        return result;
    }

}