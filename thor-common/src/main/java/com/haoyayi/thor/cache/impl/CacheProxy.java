package com.haoyayi.thor.cache.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.haoyayi.thor.cache.redis.JedisCallback;

/**
 * Created by xiaoyuan on 3/16/15.
 */
public class CacheProxy {

    public static Long getAsLong(final String key) {

        Long res = CacheTemplate.execute(new JedisCallback<Long>() {

            @Override
            public Long call(Jedis jedis) {
                Long result = null;
                String value = jedis.get(key);
                if (value != null) {
                    result = Long.valueOf(value);
                }
                return result;
            }
        });

        return res;
    }

    public static Integer getAsInteger(final String key) {
        Integer res = CacheTemplate.execute(new JedisCallback<Integer>() {

            public Integer call(Jedis jedis) {
                Integer result = null;
                String value = jedis.get(key);
                if (value != null) {
                    result = Integer.valueOf(value);
                }
                return result;
            }
        });

        return res;
}

    public static String getAsString(final String key) {
        String res = CacheTemplate.execute(new JedisCallback<String>() {

            public String call(Jedis jedis) {
                return jedis.get(key);
            }
        });

        return res;
    }
    
    public static boolean exist(final String key) {
        boolean res = CacheTemplate.execute(new JedisCallback<Boolean>() {

            public Boolean call(Jedis jedis) {
                Boolean result = false;
                String value = jedis.get(key);
                if (value != null) {
                    result = true;
                }
                return result;
            }
        });

        return res;

    }

    /**
     * expire<0表示永久不过期, 否则设置过期时间,
     * 单位为秒
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public static boolean set(final String key, final String value, final int expire) {
        Boolean res = CacheTemplate.execute(new JedisCallback<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                jedis.set(key, value);
                if (expire > 0) jedis.expire(key, expire);
                return true;
            }
        });

        return res;
    }
    
    public static boolean del(final String key) {
        Boolean res = CacheTemplate.execute(new JedisCallback<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                return jedis.del(key) > 0;
            }
        });

        return res;
    }

    /**
     * 如果key不存在就设置，返回true
     * 如果key存在，不设置，返回false
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public static boolean setNXEX(final String key, final String value, final int expire) {
    	 	Boolean res = CacheTemplate.execute(new JedisCallback<Boolean>() {
             @Override
             public Boolean call(Jedis jedis) {
                 String result = jedis.set(key, value, "NX", "EX", expire);
                 boolean ret = false;
                 if(result != null) {
                     ret = true;
                 }
                 return ret;
             }
         });

         return res;
    }

    public static boolean rpush(final String key, final String value) {
    	return CacheTemplate.execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean call(Jedis jedis) throws Exception {
				return jedis.rpush(key, value) > 0;
			}
		});
    }
    
    public static Set<String> keys(final String pattern) {
    	return CacheTemplate.execute(new JedisCallback<Set<String>>() {
			@Override
			public Set<String> call(Jedis jedis) throws Exception {
				return jedis.keys(pattern);
			}
		});
    }
    
    public static String lpop(final String key) {
    	return CacheTemplate.execute(new JedisCallback<String>() {
			@Override
			public String call(Jedis jedis) throws Exception {
				return jedis.lpop(key);
			}
		});
    }
    
    public static List<String> lrange(final String key, final int start, final int end) {
    	return CacheTemplate.execute(new JedisCallback<List<String>>() {
			@Override
			public List<String> call(Jedis jedis) throws Exception {
				return jedis.lrange(key, start, end);
			}
		});
    }
    
    public static boolean lrem(final String key, final String value) {
    	return CacheTemplate.execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean call(Jedis jedis) throws Exception {
				return jedis.lrem(key, 1, value) > 0;
			}
		});
    }
    
    public static boolean hset(final String key, final String field, final String value) {
    	return CacheTemplate.execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean call(Jedis jedis) throws Exception {
				return jedis.hset(key, field, value) > 0;
			}
		});
    }
    
    public static Long hdel(final String key, final String field) {
	    	return CacheTemplate.execute(new JedisCallback<Long>() {
	    		@Override
	    		public Long call(Jedis jedis) throws Exception {
	    			Long value = jedis.hdel(key, field);
	    			return value;
	    		}
	    	});
    }
    
    public static String hget(final String key, final String field) {
    	return CacheTemplate.execute(new JedisCallback<String>() {
			@Override
			public String call(Jedis jedis) throws Exception {
				return jedis.hget(key, field);
			}
		});
    }
    
    public static List<String> hmget(final String key, final String... fields) {
    	return CacheTemplate.execute(new JedisCallback<List<String>>() {
			@Override
			public List<String> call(Jedis jedis) throws Exception {
				return jedis.hmget(key, fields);
			}
		});
    }
    
    public static String hmset(final String key, final Map<String, String> values) {
    	return CacheTemplate.execute(new JedisCallback<String>() {
			@Override
			public String call(Jedis jedis) throws Exception {
				return jedis.hmset(key, values);
			}
		});
    }
}
