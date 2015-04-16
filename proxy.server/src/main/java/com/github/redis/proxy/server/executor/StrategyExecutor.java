package com.github.redis.proxy.server.executor;

import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.BackendExecutorContext;
import com.github.redis.proxy.server.net.backend.BackendConnectionFactory;

public abstract class StrategyExecutor
{
    protected BackendConnectionFactory factory = BackendConnectionFactory.instance();

    abstract void execute(BackendExecutorContext context) throws ProxyException;
}
