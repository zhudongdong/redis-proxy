package com.github.redis.proxy.server.net.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.redis.proxy.server.config.AbstractHost;
import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.DataNodes;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.config.ReadHost;
import com.github.redis.proxy.server.config.WriteHost;

public class BackendConnectionFactory
{
    private static final BackendConnectionFactory INSTANCE = new BackendConnectionFactory();

    private final Map<AbstractHost, BackendConnectionPool> connectionMap = new HashMap<>();

    private BackendConnectionFactory()
    {
        DataNodes dataNodes = ConfigHolder.instance().getDataNodes();
        List<DataNode> dataNodeList = dataNodes.getDataNodes();
        for (DataNode dataNode : dataNodeList)
        {
            Host host = dataNode.getHost();
            WriteHost writeHost = host.getWriteHost();
            BackendConnectionPool writePool = new BackendConnectionPool(host.getWriteHost());
            connectionMap.put(writeHost, writePool);
            List<ReadHost> readHosts = host.getReadHosts();
            for (ReadHost readHost : readHosts)
            {
                BackendConnectionPool readPool = new BackendConnectionPool(readHost);
                connectionMap.put(readHost, readPool);
            }
        }
        startCheck();
    }

    private void startCheck()
    {
        for (AbstractHost host : connectionMap.keySet())
        {
            BackendConnectionPool connectionPool = connectionMap.get(host);
            connectionPool.doHeartbeat();
        }
    }

    public static BackendConnectionFactory instance()
    {
        return INSTANCE;
    }

    public BackendConnection getConnection(AbstractHost host)
    {
        return connectionMap.get(host).getConnection();
    }

    public List<BackendConnection> getAllConnections()
    {
        List<BackendConnection> connections = new ArrayList<>();
        for (AbstractHost host : connectionMap.keySet())
        {
            BackendConnectionPool pool = connectionMap.get(host);
            connections.addAll(pool.getConnections());
        }
        return connections;
    }
}
