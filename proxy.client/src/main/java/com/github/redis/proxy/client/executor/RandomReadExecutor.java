package com.github.redis.proxy.client.executor;

import java.util.List;
import java.util.Random;

import redis.clients.jedis.Jedis;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.ReadHost;
import com.github.redis.proxy.server.exception.ProxyException;

/**
 * 随机选择读节点的executor
 * 
 * @author Administrator
 *
 */
public class RandomReadExecutor extends AbstractExecutor
{
    private Random random = new Random();

    public void doChain(ExecutorContext context) throws ProxyException
    {
        List<DataNode> dataNodes = context.getDataNodes();
        for (DataNode dataNode : dataNodes)
        {
            List<ReadHost> readHosts = dataNode.getHost().getReadHosts();
            ReadHost readHost = readHosts.get(random.nextInt(readHosts.size()));
            Jedis jedis = factory.getJedis(readHost);
            context.invoke(jedis);
        }
    }

}
