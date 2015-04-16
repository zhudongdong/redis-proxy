package com.github.redis.proxy.server.config.loader;

import java.util.List;

import lombok.Getter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import com.github.redis.proxy.server.exception.ConfigLoadException;

public class ZookeeperClient
{
    private static final int CONNECTION_TIMEOUT = 5000;

    private static int RETRY_TIMES = Integer.MAX_VALUE;

    private static int SLEEP_MS_BETWEEN_RETRY = 1000;
    @Getter
    private final CuratorFramework client;

    public ZookeeperClient(String urls, String namespace)
    {
        client = CuratorFrameworkFactory.builder().connectString(urls).namespace(namespace)
                .retryPolicy(new RetryNTimes(RETRY_TIMES, SLEEP_MS_BETWEEN_RETRY))
                .connectionTimeoutMs(CONNECTION_TIMEOUT).build();
        client.start();
    }

    public List<String> children(String parentPath)
    {
        try
        {
            return client.getChildren().forPath(parentPath);
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public void watch(String path, CuratorWatcher watcher)
    {
        try
        {
            client.getData().usingWatcher(watcher).forPath(path);
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public void watch(String path, Watcher watcher)
    {
        try
        {
            client.getData().usingWatcher(watcher).forPath(path);
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public String getData(String string)
    {
        try
        {
            return new String(client.getData().forPath(string));
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public boolean checkExists(String path)
    {
        try
        {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public String createPath(String path, String content, CreateMode createMode)
    {
        try
        {
            return client.create().withMode(createMode).forPath(path, content == null ? null : content.getBytes());
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public void deletePath(String path)
    {
        try
        {
            client.delete().forPath(path);
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }

    public void setData(String path, String content)
    {
        try
        {
            client.setData().forPath(path, content.getBytes());
        } catch (Exception e)
        {
            throw new ConfigLoadException(e);
        }
    }
}
