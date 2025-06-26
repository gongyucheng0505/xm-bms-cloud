package com.xm.bms.carinfo.config;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class JedisClient {

    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    @PreDestroy
    public void destroy() {
        jedisPool.close();
    }
}
