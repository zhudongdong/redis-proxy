package com.github.redis.proxy.client.executor;

import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.server.config.DataNode;

public interface Executable
{
    void invoke(ExecutorContext context, DataNode dataNode);
}
