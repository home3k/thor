package com.haoyayi.thor.cache.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.haoyayi.disconf.api.DisconfClient;
import com.haoyayi.disconf.factory.DisconfClientFactory;
import com.haoyayi.thor.disconf.NormalChangedCallback;

/**
 * Created by xiaoyuan on 3/16/15.
 */
public class JedisPoolFactory {
    private static JedisPoolFactory INSTANCE = new JedisPoolFactory();

    //redis pool 池子
    private List<JedisPool> pools;

    private int retryCount;


    public JedisPool getPool() {
        return pools.get(0);
    }
    
    private void initAttr(DisconfClient client) {
    	pools = new ArrayList<JedisPool>();
        try {
        	String host = client.getConfString("redis.properties", "redis.host");
        	String password = client.getConfString("redis.properties", "redis.password");
            int port = client.getConfInt("redis.properties", "redis.port");
            int timeout = client.getConfInt("redis.properties", "redis.timeout");
            int retrycount = client.getConfInt("redis.properties", "redis.retry");
            this.retryCount = retrycount;

            JedisPool p = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
            pools.add(p);
        } catch (Exception e) {
            throw new RuntimeException("init JedisPool error!!", e);
        }
    }

    private JedisPoolFactory() {
    	final DisconfClient client = DisconfClientFactory.getClient("disconf.properties");
        initAttr(client);
        client.addConfListener("thor#redis.properties", new NormalChangedCallback() {
			@Override
			public void fileChanged(String newValue) {
				initAttr(client);
			}
		});
    }

    public static synchronized JedisPoolFactory getInstance() {
        return INSTANCE;
    }


    public int getRetryCount() {
        return retryCount;
    }

}
