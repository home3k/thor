package com.haoyayi.thor.cache.impl;

import com.haoyayi.thor.cache.redis.JedisCallback;
import com.haoyayi.thor.cache.redis.JedisPoolFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by xiaoyuan on 3/16/15.
 */
public class CacheTemplate {

    public static final int UCENTER_INDEX = 1;
    public static final int THOR_INDEX = 3;

    public static <T> T execute(JedisCallback<T> callBack) {
        T result = null;

        JedisPoolFactory factory = JedisPoolFactory.getInstance();
        int retryCount = factory.getRetryCount();

        for (int i = 0; i <= retryCount; i++) {
            JedisPool jedisPool = factory.getPool();
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.select(THOR_INDEX);
                result = callBack.call(jedis);
                return result;
            } catch (Exception e) {

                if (null != jedis) {
                    jedisPool.returnBrokenResource(jedis);
                    jedis = null;
                }
            } finally {
                if (null != jedis)
                    jedisPool.returnResource(jedis);
            }
        }

        throw new RuntimeException("invoke jedis pool error.");

    }
}
