package com.github.redis.proxy.server.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;
import com.github.redis.proxy.server.net.front.FrontConnection;

public class FrontExecutor implements Executor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontExecutor.class);

    public void execute(ExecutorEvent event) throws ProxyException
    {
        FrontExecutorContext context = event.getSource();
        FrontConnection connection = context.getFrontConnection();
        connection.write(context);
        LOGGER.debug("[FrontExecutor   .{}]写入前端队列，对应连接{}", context, connection);
    }

}
