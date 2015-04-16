package com.github.redis.proxy.server.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.config.AbstractHost;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.BackendExecutorContext;
import com.github.redis.proxy.server.net.backend.BackendConnection;

public class WriteOnlyExecutor extends StrategyExecutor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteOnlyExecutor.class);

    public void execute(BackendExecutorContext context) throws ProxyException
    {
        Host host = context.getDataNode().getHost();
        AbstractHost targetHost = host.getWriteHost();
        BackendConnection backendConnection = factory.getConnection(targetHost);
        LOGGER.debug("[BackendExecutor   .{}]写入后端队列.BackendExecutorContext:{},对应连接：{}", context.getParent(), context,
                backendConnection);
        backendConnection.write(context);
    }
}
