package com.haoyayi.thor.cache.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by xiaoyuan on 3/16/15.
 */
public interface JedisCallback<T> {
    public T call(Jedis jedis) throws Exception;
}
