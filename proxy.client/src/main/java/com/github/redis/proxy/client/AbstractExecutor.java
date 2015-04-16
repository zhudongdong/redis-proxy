package com.github.redis.proxy.client;

import com.github.redis.proxy.client.executor.Executable;
import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.JedisConnectionFactory;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.route.hash.ProxyHashing;

public abstract class AbstractExecutor implements Executor, Executable
{
    protected static JedisConnectionFactory factory = JedisConnectionFactory.instance();

    protected static ConfigHolder holder = ConfigHolder.instance();

    protected ProxyHashing hashing = new ProxyHashing();

    public void invoke(ExecutorContext context, DataNode dataNode)
    {
        throw new ProxyException("I am an adapter.");
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        throw new ProxyException("I am an adapter.");
    }
}
