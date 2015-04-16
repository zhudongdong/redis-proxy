package com.github.redis.proxy.server.config.loader;

import java.io.IOException;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;

import com.alibaba.fastjson.JSONObject;
import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.DataNodes;
import com.github.redis.proxy.server.exception.ConfigLoadException;

public class ZookeeperLoader implements ConfigLoader
{
    private NodeCacheListener nodeCacheListener = new DataListener();

    private NodeCache cache;

    private final ZookeeperClient client;

    public ZookeeperLoader(String urls)
    {
        client = new ZookeeperClient(urls, "proxyconfig");
        cache = new NodeCache(client.getClient(), "/datanode");
        registerListener();
    }

    public void upload(DataNodes dataNodes) throws Exception
    {
        if (!client.checkExists("/datanode"))
        {
            client.createPath("/datanode", null, CreateMode.PERSISTENT);
        }
        String data = JSONObject.toJSONString(dataNodes);
        client.setData("/datanode", data);
    }

    public DataNodes load() throws ConfigLoadException
    {
        String data = client.getData("/datanode");
        DataNodes dataNodes = JSONObject.parseObject(data, DataNodes.class);
        return dataNodes;
    }

    private void registerListener()
    {
        try
        {
            cache.getListenable().addListener(nodeCacheListener);
            cache.start(true);
        } catch (Exception e)
        {
            try
            {
                cache.close();
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    private class DataListener implements NodeCacheListener
    {
        public void nodeChanged() throws Exception
        {
            // 节点发生改变，重新加载配置文件
            ConfigHolder.instance().reload();
        }
    }

    public static void main(String[] args) throws Exception
    {
        ZookeeperLoader loader = new ZookeeperLoader("localhost:2181");
        ProxyConfigLoader loader2 = new ProxyConfigLoader("/proxy-config.xml");
        DataNodes dataNodes = loader2.load();
        loader.upload(dataNodes);
    }
}
