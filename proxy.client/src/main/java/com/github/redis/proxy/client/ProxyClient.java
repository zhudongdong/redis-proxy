package com.github.redis.proxy.client;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.github.redis.proxy.client.proxy.JedisProxy;
import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.DataNode;

public class ProxyClient
{
    public static void main(String[] args) throws Exception
    {
        // Jedis jedis = new Jedis("localhost",6666);
        Jedis jedis1 = JedisProxy.proxy();
        Map<Integer, DataNode> slotMap = ConfigHolder.instance().getSlotMap();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(300);
        config.setMaxTotal(500);
        JedisPool readPool = new JedisPool(config, "172.31.22.84", 6379);
        long b = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++)
        {
            long begin = System.nanoTime();
             Jedis jedis = JedisProxy.proxy();
//            Jedis jedis = readPool.getResource();
            jedis.set("" + i, "" + i);
//            jedis.close();
            long end = System.nanoTime();
            System.out.println(end - begin);
        }
        long e = System.currentTimeMillis();
        System.out.println((double) (10000) / (double) (e - b) * 1000);
    }
}
