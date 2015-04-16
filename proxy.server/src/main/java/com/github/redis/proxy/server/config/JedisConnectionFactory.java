package com.github.redis.proxy.server.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConnectionFactory
{
    private Map<AbstractHost, JedisPool> connectionMap;

    private static final JedisConnectionFactory INSTANCE = new JedisConnectionFactory();

    private JedisConnectionFactory()
    {
        connectionMap = load(ConfigHolder.instance().getDataNodes());
    }

    private Map<AbstractHost, JedisPool> load(DataNodes dataNodes)
    {
        Map<AbstractHost, JedisPool> map = new ConcurrentHashMap<AbstractHost, JedisPool>();
        List<DataNode> dataNodeList = dataNodes.getDataNodes();
        for (DataNode dataNode : dataNodeList)
        {
            Host host = dataNode.getHost();
            JedisPoolConfig config = buildPoolConfig(host);
            WriteHost writeHost = host.getWriteHost();
            JedisPool writePool = new JedisPool(config, writeHost.getHost(), writeHost.getPort());
            map.put(writeHost, writePool);
            List<ReadHost> readHosts = host.getReadHosts();
            for (ReadHost readHost : readHosts)
            {
                JedisPool readPool = new JedisPool(config, readHost.getHost(), readHost.getPort());
                map.put(readHost, readPool);
            }
        }
        return map;
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

    boolean reload(DataNodes dataNodes)
    {
        try
        {
            Map<AbstractHost, JedisPool> newMap = load(dataNodes);
            //清理资源
            for (Iterator<AbstractHost> iterator = connectionMap.keySet().iterator(); iterator.hasNext();)
            {
                AbstractHost key = iterator.next();
                JedisPool pool = connectionMap.get(key);
                try
                {
                    pool.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            //切换
            connectionMap = newMap;
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
