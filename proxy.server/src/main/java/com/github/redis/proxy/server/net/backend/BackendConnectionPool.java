package com.github.redis.proxy.server.net.backend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

import com.github.redis.proxy.server.config.AbstractHost;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.net.backend.heartbeat.Heartbeat;
import com.github.redis.proxy.server.net.backend.heartbeat.ProxyHeartbeat;
import com.google.common.base.Preconditions;

public class BackendConnectionPool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendConnectionPool.class);
    private static final int channelCount = 200;
    private static final int heartbeatPeriod = 10000;
    private AbstractHost host;

    private BackendConnectHandler connectHandler = new BackendConnectHandler();
    @Getter
    private List<BackendConnection> connections = new ArrayList<BackendConnection>();

    private Random random = new Random(System.currentTimeMillis());

    private AtomicBoolean isValid = new AtomicBoolean(true);

    private Heartbeat proxyHeartbeat;

    public BackendConnectionPool(AbstractHost host)
    {
        Preconditions.checkNotNull(host, "host 不能为空");
        this.host = host;
        try
        {
            proxyHeartbeat = new ProxyHeartbeat(host.getHost(), host.getPort());
        } catch (IOException e)
        {
            throw new ProxyException(
                    "backend connection pool initialization failed. can not get connection from the host:" + host);
        }
        init();
    }

    public BackendConnection getConnection()
    {
        BackendConnection connection = null;
        while (isValid.get())
        {
            if (connections.size() > 0)
            {
                if (!(connection = connections.get(random.nextInt(connections.size()))).isClosed())
                {
                    return connection;
                }
            }
            throw new ProxyException("no avaliable backend connection can be get from the host:" + host);
        }
        throw new ProxyException("backend connection pool has beeb closed.can not get connection from the host:" + host);
    }

    private void init()
    {
        creatConnection(channelCount);
    }

    private void creatConnection(int count)
    {
        for (int i = 0; i < count; i++)
        {
            AsynchronousSocketChannel channel;
            try
            {
                channel = AsynchronousSocketChannel.open();
                channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                BackendConnection connection = new BackendConnection(channel, this);
                connections.add(connection);
                channel.connect(new InetSocketAddress(host.getHost(), host.getPort()), connection, connectHandler);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void recycle(BackendConnection backendConnection)
    {
        connections.remove(backendConnection);
    }

    public void doHeartbeat()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                if (!proxyHeartbeat.heartbeat())
                {
                    LOGGER.error("backend connection pool has been closed,please check the redis server:" + host);
                    if (isValid.compareAndSet(true, false))
                    {
                        connections.clear();
                    }
                } else
                {
                    isValid.set(true);
                    creatConnection(channelCount - connections.size());
                }
            }
        }, 0, heartbeatPeriod);
    }
}
