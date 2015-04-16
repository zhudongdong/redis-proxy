package com.github.redis.test;

import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.github.redis.proxy.client.JedisAdapter;
import com.github.redis.proxy.client.proxy.JedisProxy;

public class ProxyClient
{
    public static void main(String[] args) throws Exception
    {
        // Jedis jedis = new Jedis("localhost",6666);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(300);
        config.setMaxTotal(500);
        final Jedis jedis = JedisProxy.proxy();
        jedis.set("a", "b");
        final JedisPool readPool = new JedisPool(config, "172.31.22.84", 6666);
//        final JedisPool readPool = new JedisPool(config, "localhost", 6666);
        final int count = 1000000;
        int thread = 32;
        final CountDownLatch latch = new CountDownLatch(thread);
        long start = System.currentTimeMillis();
        for (int i = 0; i < thread; i++)
        {
            final int index = i;
            new Thread()
            {
                public void run()
                {
                    for (int i = index * count; i < (index + 1) * count; i++)
                    {
                        Jedis jedis = JedisProxy.proxy();
//                        Jedis jedis = readPool.getResource();
                        jedis.set("" + i, "" + i);
//                        System.out.println(jedis.set("" + i, "" + i));
//                        jedis.close();
                    }
                    latch.countDown();
                };
            }.start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println((double) (count * thread) / (double) ((end - start)) * 1000);
        readPool.close();
    }
}
