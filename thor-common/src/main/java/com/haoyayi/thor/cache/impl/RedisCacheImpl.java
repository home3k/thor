///*
// * Copyright 2015 51haoyayi.com Inc Limited.
// * All rights reserved.
// */
//
//package com.haoyayi.thor.cache.impl;
//
//import com.haoyayi.thor.cache.Cache;
//import com.haoyayi.thor.cache.serializer.Serializer;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.pool.impl.GenericObjectPool;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import redis.clients.jedis.*;
//import redis.clients.util.Hashing;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * @author home3k (sunkai@51haoyayi.com)
// */
//public class RedisCacheImpl implements Cache {
//
//    private static Logger log = LoggerFactory.getLogger(RedisCacheImpl.class);
//
//    private static final String CACHE_KEY_PREFIX = "";
//
//    // Jedis Client group
//    private ShardedJedisPool pool = null;
//
//    // Default operation timeout in seconds.
//    private static final int DEFAULT_REDIS_TIMEOUT = 1;
//
//    // retry number
//    private static final int DEFAULT_REDIS_RETRY = 3;
//
//    private static final int DEFAULT_MAX_IDLE = 20;
//
//    private static final int DEFAULT_MAX_WAIT = 1000;
//
//    private static final int DEFAULT_MAX_ACTIVE = 20;
//
//    private static final Boolean DEFAULT_TEST_ON_BORROW = true;
//
//    private static final int DEFAULT_EXP_SEC = 2;
//
//    private int opTimeout = DEFAULT_REDIS_TIMEOUT;
//
//    private int retry = DEFAULT_REDIS_RETRY;
//
//    private int maxIdle = DEFAULT_MAX_IDLE;
//
//    private int maxWait = DEFAULT_MAX_WAIT;
//
//    private int maxActive = DEFAULT_MAX_ACTIVE;
//
//    private boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;
//
//    private Serializer serializer;
//
//    private String password = null;
//
//    private int exp = DEFAULT_EXP_SEC;
//
//    public RedisCacheImpl(String fileName) {
//        init(fileName);
//    }
//
//    private void init(String fileName) {
//
//        Properties configParam = config(fileName);
//
//        // ------------load redis server addr-----------------------
//        String server = configParam.getProperty("server");
//
//        // fill redis property
//        opTimeout = Integer.parseInt(configParam.getProperty("opTimeout"));
//        retry = Integer.parseInt(configParam.getProperty("retry"));
//        exp = Integer.parseInt(configParam.getProperty("expSec"));
//        maxActive = Integer.parseInt(configParam.getProperty("maxActive"));
//        maxIdle = Integer.parseInt(configParam.getProperty("maxIdle"));
//        maxWait = Integer.parseInt(configParam.getProperty("maxWait"));
//        testOnBorrow = Boolean.parseBoolean(configParam.getProperty("testOnBorrow"));
//        password = configParam.getProperty("password");
//
//        //pool config
//        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
//        poolConfig.maxIdle = maxIdle;
//        poolConfig.maxWait = maxWait;
//        poolConfig.maxActive = maxActive;
//        poolConfig.testOnBorrow = testOnBorrow;
//
//        int failCount = 0;
//
//        PoolFactory poolFactory = new PoolFactory();
//
//        try {
//            pool = poolFactory.creativePool(poolConfig, "pool1:", server,
//                    password, opTimeout);
//        } catch (Exception e) {
//            failCount++;
//            log.error(" Init redis client 1 failed!!");
//        }
//
//        // failed.
//        if (failCount == 1) {
//            throw new RuntimeException("All redis Clients init failed!!");
//        }
//    }
//
//    private Properties config(String fileName) {
//
//        Properties prop = new Properties();
//
//        try {
//            prop.load(RedisCacheImpl.class.getClassLoader()
//                    .getResourceAsStream(fileName + ".properties"));
//        } catch (IOException e) {
//            log.info("Load file: " + fileName + "failed!!! ");
//            throw new RuntimeException("Load properties file: " + fileName
//                    + " failed!!!");
//        }
//
//        return prop;
//    }
//
//
//    public void asynRemove(Set<String> keys) {
//        this.remove(keys);
//    }
//
//    @Override
//    public void asynSet(Map<String, Object> items) {
//        this.set(items);
//    }
//
//    @Override
//    public void asynSet(Map<String, Object> items, Integer expTime) {
//        this.set(items, expTime);
//    }
//
//    public Object get(String key) {
//        Set<String> keys = new HashSet<String>();
//        keys.add(key);
//        Map<String, Object> map = this.get(keys);
//        if (null == map || map.size() == 0) {
//            return null;
//        }
//        return map.values().iterator().next();
//    }
//
//    public Object get(String key, Class<?> clazz) {
//        Set<String> keys = new HashSet<String>();
//        keys.add(key);
//        Map<String, Object> map = this.get(keys, clazz);
//        if (null == map || map.size() == 0) {
//            return null;
//        }
//        return map.values().iterator().next();
//    }
//
//    /**
//     */
//    @Override
//    public Map<String, Object> get(Set<String> keys) {
//        return this.get(keys, Object.class);
//    }
//
//    public boolean remove(String key) {
//        Set<String> keys = new HashSet<String>();
//        keys.add(key);
//        return remove(keys);
//    }
//
//
//    /**
//     */
//    public boolean remove(Set<String> keys) {
//
//        Set<String> processedKeys = processKey(keys);
//
//        boolean ret = false;
//
//        boolean fs = remove(pool, processedKeys);
//
//        log.debug("[REMOVE]" + ret + "\tKey=" + keys);
//
//        return fs;
//    }
//
//    private boolean remove(ShardedJedisPool pool, Set<String> keys) {
//        if (keys == null || keys.isEmpty()) {
//            return false;
//        }
//        ShardedJedis shardedJedis = null;
//        try {
//            shardedJedis = pool.getResource();
//            Map<String, Jedis> key4jedis = this.getJedisClient(shardedJedis,
//                    keys);
//            Map<Jedis, Pipeline> jedis4pipeline = new HashMap<Jedis, Pipeline>();
//            for (String key : key4jedis.keySet()) {
//                byte[] bkey = serializer.serialize(key, String.class);
//                Jedis jedis = key4jedis.get(key);
//                Pipeline pipeline = jedis4pipeline.get(jedis);
//                if (null == pipeline) {
//                    pipeline = jedis.pipelined();
//                    jedis4pipeline.put(jedis, pipeline);
//                }
//                pipeline.del(bkey);
//            }
//            for (Pipeline pipeline : jedis4pipeline.values()) {
//                pipeline.sync();
//            }
//        } catch (Exception e) {
//            log.info("[FAIL]exception when getting object from cache "
//                    + pool.toString() + "...");
//            return false;
//        } finally {
//            if (null != shardedJedis)
//                pool.returnResource(shardedJedis);
//        }
//        return true;
//    }
//
//    private String processKey(String key) {
//        return CACHE_KEY_PREFIX + key;
//    }
//
//    private Set<String> processKey(Set<String> keys) {
//        Set<String> processedKeys = new HashSet<String>();
//        for (String key : keys) {
//            if (key != null) {
//                processedKeys.add(processKey(key));
//            }
//        }
//        return processedKeys;
//    }
//
//    private Map<String, Object> processItem(Map<String, Object> items) {
//        Map<String, Object> processedItems = new HashMap<String, Object>();
//        for (String key : items.keySet()) {
//            if (key != null) {
//                processedItems.put(processKey(key), items.get(key));
//            }
//        }
//        return processedItems;
//    }
//
//    public Map<String, Object> get(Set<String> keys, Class<?> clazz) {
//
//        Set<String> processedKeys = processKey(keys);
//        Map<String, Object> resInCache = null;
//
//        for (int i = 0; i < retry; i++) {
//            try {
//                resInCache = get(pool, processedKeys, clazz);
//            } catch (Exception e) {
//                log.error("[ERROR]other exception when getting objects from redis cache...", e);
//            }
//            if (resInCache != null) {
//                break;
//            }
//        }
//
//        if (resInCache != null) {
//            StringBuilder logMsg = new StringBuilder("[GET]SHOOTED");
//            for (String key : keys) {
//                Object value = resInCache.get(key);
//                if (value != null) {
//                    if (value != null) {
//                        logMsg.append("\tKey=").append(key).append("\tValue=").append(value);
//                    }
//                }
//            }
//            log.debug(logMsg.toString());
//        }
//
//        return resInCache;
//    }
//
//    private Map<String, Object> get(ShardedJedisPool pool, Set<String> keys,
//                                    Class<?> clazz) {
//        Map<String, Object> result = new HashMap<String, Object>();
//        ShardedJedis shardedJedis = null;
//        try {
//            shardedJedis = pool.getResource();
//            Map<String, Jedis> key4jedis = this.getJedisClient(shardedJedis,
//                    keys);
//            Map<Jedis, Pipeline> jedis4pipeline = new HashMap<Jedis, Pipeline>();
//            Map<Pipeline, List<String>> pipeline4keys = new HashMap<Pipeline, List<String>>();
//            for (String key : key4jedis.keySet()) {
//                byte[] bkey = serializer.serialize(key, String.class);
//                Jedis jedis = key4jedis.get(key);
//
//                Pipeline pipeline = jedis4pipeline.get(jedis);
//                if (null == pipeline) {
//                    pipeline = jedis.pipelined();
//                    jedis4pipeline.put(jedis, pipeline);
//                }
//                List<String> resultKeys = pipeline4keys.get(pipeline);
//                if (null == resultKeys) {
//                    resultKeys = new ArrayList<String>();
//                }
//                resultKeys.add(key);
//                pipeline.get(bkey);
//                pipeline4keys.put(pipeline, resultKeys);
//            }
//            for (Pipeline pipeline : jedis4pipeline.values()) {
//                List<String> pkeys = pipeline4keys.get(pipeline);
//                List<Object> pres = pipeline.syncAndReturnAll();
//                for (int i = 0; i < pkeys.size(); i++) {
//                    String key = pkeys.get(i);
//                    Object res = pres.get(i);
//                    if (null == res) {
//                        result.put(key, res);
//                    } else {
//                        Object value = serializer.deserialize((byte[]) res,
//                                clazz);
//                        result.put(key, value);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("[FAIL]exception when getting object from cache "
//                    + pool.toString() + "...");
//            return null;
//        } finally {
//            if (null != shardedJedis)
//                pool.returnResource(shardedJedis);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean set(Map<String, Object> items) {
//        boolean result = false;
//        for (int i = 0; i < retry; i++) {
//            result = set(items, exp);
//            if (result) {
//                break;
//            }
//        }
//        return result;
//    }
//
//    public boolean set(String key, Object value) {
//        Map<String, Object> items = new HashMap<String, Object>();
//        items.put(key, value);
//        return set(items);
//    }
//
//
//    public boolean set(Map<String, Object> items, Integer expTime) {
//        boolean fs = false;
//
//        items = processItem(items);
//
//        fs = set(pool, items, expTime);
//
//        return fs;
//    }
//
//    private boolean set(ShardedJedisPool pool, Map<String, Object> items,
//                        Integer expTime) {
//        ShardedJedis shardedJedis = null;
//        try {
//            shardedJedis = pool.getResource();
//            Map<String, Jedis> key4jedis = this.getJedisClient(shardedJedis,
//                    items.keySet());
//            Map<Jedis, Pipeline> jedis4pipeline = new HashMap<Jedis, Pipeline>();
//            for (String key : key4jedis.keySet()) {
//                byte[] bkey = serializer.serialize(key, String.class);
//                Jedis jedis = key4jedis.get(key);
//                Pipeline pipeline = jedis4pipeline.get(jedis);
//                if (null == pipeline) {
//                    pipeline = jedis.pipelined();
//                    jedis4pipeline.put(jedis, pipeline);
//                }
//                Object value = items.get(key);
//                pipeline.set(bkey, serializer
//                        .serialize(value, value.getClass()));
//                pipeline.expire(bkey, expTime);
//            }
//            for (Pipeline pipeline : jedis4pipeline.values()) {
//                pipeline.sync();
//            }
//        } catch (Exception e) {
//            log.error("[FAIL]exception when seting object to cache "
//                    + pool.toString() + "...");
//            return false;
//        } finally {
//            if (null != shardedJedis)
//                pool.returnResource(shardedJedis);
//        }
//        return true;
//    }
//
//    /**
//     * Getter method for property <tt>serializer</tt>
//     *
//     * @return the property value of serializer
//     */
//    public Serializer getSerializer() {
//        return serializer;
//    }
//
//    /**
//     * Setter method for property <tt>serializer</tt>
//     *
//     * @param serializer value to be assigned to property serializer
//     */
//    public void setSerializer(Serializer serializer) {
//        this.serializer = serializer;
//    }
//
//    class PoolFactory {
//
//        public ShardedJedisPool creativePool(GenericObjectPool.Config poolConfig, String name,
//                                             String server, String password, int timeout) {
//            String[] servers = StringUtils.split(server, " ");
//            if (null == servers || servers.length == 0) {
//                throw new RuntimeException(
//                        "jedis client init failed!! the servers is empty!!");
//            }
//            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
//            int i = 1;
//            for (String shardServer : servers) {
//                String[] iport = StringUtils.split(shardServer, ":");
//                String ip = iport[0];
//                String port = iport[1];
//                if (2 != iport.length || null == ip || null == port) {
//                    throw new RuntimeException(
//                            "jedis client init failed!! the servers is empty!!");
//                }
//                JedisShardInfo shardInfo = new JedisShardInfo(ip, Integer
//                        .parseInt(port.trim()), timeout, name + i);
//                shardInfo.setPassword(password);
//                shards.add(shardInfo);
//                i++;
//            }
//            ShardedJedisPool pool = new ShardedJedisPool(poolConfig, shards,
//                    Hashing.MURMUR_HASH);
//            return pool;
//
//        }
//    }
//
//    private Map<String, Jedis> getJedisClient(ShardedJedis shardedJedis,
//                                              Set<String> keys) {
//        Map<String, Jedis> key4Jedis = new HashMap<String, Jedis>();
//        for (String key : keys) {
//            key4Jedis.put(key, shardedJedis.getShard(key));
//        }
//        return key4Jedis;
//    }
//
//    @Override
//    public void asynRemove(Collection<String> keys) {
//
//    }
//
//    @Override
//    public void remove(Collection<String> keys) {
//
//    }
//
//    @Override
//    public void asynSet(Map<String, Object> kv) {
//
//    }
//
//    @Override
//    public void set(Map<String, Object> kv) {
//
//    }
//
//    @Override
//    public void set(Map<String, Object> items, Integer expTime) {
//
//    }
//
//    @Override
//    public void asynSet(Map<String, Object> items, Integer expTime) {
//
//    }
//
//    @Override
//    public <T> Map<String, T> get(Set<String> keys, Class<T> clazz) {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> get(Set<String> keys) {
//        return null;
//    }
//}
//}
//
//
//
//
