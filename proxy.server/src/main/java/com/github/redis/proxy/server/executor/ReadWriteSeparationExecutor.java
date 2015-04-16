package com.github.redis.proxy.server.executor;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.config.AbstractHost;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.config.ReadHost;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.BackendExecutorContext;
import com.github.redis.proxy.server.net.backend.BackendConnection;

public class ReadWriteSeparationExecutor extends StrategyExecutor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadWriteSeparationExecutor.class);
    private Random random = new Random(System.currentTimeMillis());

    public void execute(BackendExecutorContext context) throws ProxyException
    {
        Host host = context.getDataNode().getHost();
        AbstractHost targetHost = null;
        if (context.getParent().isRead())
        {
            List<ReadHost> readHosts = host.getReadHosts();
            if (readHosts == null || readHosts.size() == 0)
            {
                throw new ProxyException("no readhosts configured." + host);
            }
            targetHost = readHosts.get(random.nextInt(readHosts.size()));
        } else
        {
            targetHost = host.getWriteHost();
        }
        BackendConnection backendConnection = factory.getConnection(targetHost);
        LOGGER.debug("[BackendExecutor   .{}]写入后端队列.BackendExecutorContext:{},对应连接：{}", context.getParent(), context,
                backendConnection);
        backendConnection.write(context);
    }
}
