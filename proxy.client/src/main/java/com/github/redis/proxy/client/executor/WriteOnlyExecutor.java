package com.github.redis.proxy.client.executor;

import redis.clients.jedis.Jedis;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.WriteHost;

public class WriteOnlyExecutor extends AbstractExecutor
{
    public WriteOnlyExecutor()
    {
    }

    public void invoke(ExecutorContext context, DataNode dataNode)
    {
        WriteHost writeHost = dataNode.getHost().getWriteHost();
        Jedis jedis = factory.getJedis(writeHost);
        context.invoke(jedis);
    }
}
