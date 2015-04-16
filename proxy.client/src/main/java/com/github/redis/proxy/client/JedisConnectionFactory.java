package com.github.redis.proxy.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.github.redis.proxy.server.config.AbstractHost;
import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.DataNodes;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.config.ReadHost;
import com.github.redis.proxy.server.config.WriteHost;

public class JedisConnectionFactory
{
    private final Map<AbstractHost, JedisPool> connectionMap = new HashMap<>();

    private static final JedisConnectionFactory INSTANCE = new JedisConnectionFactory();

    private JedisConnectionFactory()
    {
        DataNodes dataNodes = ConfigHolder.instance().getDataNodes();
        List<DataNode> dataNodeList = dataNodes.getDataNodes();
        for (DataNode dataNode : dataNodeList)
        {
            Host host = dataNode.getHost();
            JedisPoolConfig config = buildPoolConfig(host);
            WriteHost writeHost = host.getWriteHost();
            JedisPool writePool = new JedisPool(config, writeHost.getHost(), writeHost.getPort());
            connectionMap.put(writeHost, writePool);
            List<ReadHost> readHosts = host.getReadHosts();
            for (ReadHost readHost : readHosts)
            {
                JedisPool readPool = new JedisPool(config, readHost.getHost(), readHost.getPort());
                connectionMap.put(readHost, readPool);
            }
        }
    }

    private JedisPoolConfig buildPoolConfig(Host host)
    {
        JedisPoolConfig config = new JedisPoolConfig();
        if (host.getMaxIdle() != null && !"".equals(host.getMaxIdle()))
        {
            config.setMaxIdle(Integer.parseInt(host.getMaxIdle()));
        }
        if (host.getMaxTotal() != null && !"".equals(host.getMaxTotal()))
        {
            config.setMaxTotal(Integer.parseInt(host.getMaxTotal()));
        }
        if (host.getMinIdle() != null && !"".equals(host.getMinIdle()))
        {
            config.setMinIdle(Integer.parseInt(host.getMinIdle()));
        }
        return config;
    }

    public static JedisConnectionFactory instance()
    {
        return INSTANCE;
    }

    public Jedis getJedis(AbstractHost host)
    {
        return connectionMap.get(host).getResource();
    }
}
