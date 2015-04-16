package com.github.redis.proxy.client;

import com.github.redis.proxy.server.exception.ProxyException;

public interface Executor
{
    public void doChain(ExecutorContext context) throws ProxyException;
}
