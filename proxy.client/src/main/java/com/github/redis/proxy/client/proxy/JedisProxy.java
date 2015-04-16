package com.github.redis.proxy.client.proxy;

import net.sf.cglib.proxy.Enhancer;
import redis.clients.jedis.Jedis;

public class JedisProxy
{
    private static final Jedis proxy;

    static
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Jedis.class);
        enhancer.setCallback(ProxyMethodInterceptor.INSTANCE);
        proxy = (Jedis) enhancer.create(new Class[] { String.class, int.class }, new Object[] { null, 0 });
    }

    public static Jedis proxy()
    {
        return proxy;
    }
}
