package com.github.redis.proxy.server.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

import com.github.redis.proxy.server.config.loader.ConfigLoader;
import com.github.redis.proxy.server.config.loader.ZookeeperLoader;
import com.github.redis.proxy.server.config.loader.ProxyConfigLoader;
import com.github.redis.proxy.server.config.loader.ServerConfigLoader;

public class ConfigHolder
{
    public static String proxy_config_path = "/proxy-config.xml";
    public static String server_config_path = "/server-config.xml";

    private static final ConfigHolder HOLDER = new ConfigHolder();
    @Getter
    private final Map<Integer, DataNode> slotMap = new ConcurrentHashMap<Integer, DataNode>();
    private final ConfigLoader proxyConfigLoader;
    private final ServerConfigLoader serverConfigLoader;
    @Getter
    private DataNodes dataNodes;
    @Getter
    private final Server server;

    private final AtomicBoolean isReloading = new AtomicBoolean();

    private ConfigHolder()
    {
        serverConfigLoader = new ServerConfigLoader();
        server = serverConfigLoader.load(ConfigHolder.class.getResourceAsStream(server_config_path));
        if (server.getConfigType().equals("local"))
        {
            proxyConfigLoader = new ProxyConfigLoader(server.getConfigLocation());
        } else
        {
//            proxyConfigLoader = new ZooKeeperLoader(server.getConfigLocation());
            proxyConfigLoader = new ZookeeperLoader(server.getConfigLocation());
        }
        dataNodes = proxyConfigLoader.load();
        initSlotMap();
        dataNodes.validate();
    }

    private void initSlotMap()
    {
        List<DataNode> list = dataNodes.getDataNodes();
        for (int i = 0; i < 16384; i++)
        {
            for (DataNode dataNode : list)
            {
                if (dataNode.getRange().getLow() <= i && dataNode.getRange().getHigh() >= i)
                {
                    slotMap.put(i, dataNode);
                }
            }
        }
    }

    public static ConfigHolder instance()
    {
        return HOLDER;
    }

    public DataNode getNode(Integer slot)
    {
        return slotMap.get(slot);
    }

    public boolean isReloading()
    {
        return isReloading.get();
    }

    public synchronized boolean reload()
    {
        if (!isReloading.compareAndSet(false, true))
        {
            return false;
        }
        DataNodes newDataNodes = null;
        try
        {
            newDataNodes = proxyConfigLoader.load();
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        if (JedisConnectionFactory.instance().reload(newDataNodes))
        {
            // 切换
            dataNodes = newDataNodes;
            initSlotMap();
        } else
        {
            return false;
        }
        isReloading.set(false);
        return true;
    }

    public static void main(String[] args) throws Exception
    {
        ConfigHolder.instance();
        while (true)
        {
            Thread.sleep(1000000);
        }
    }
}
