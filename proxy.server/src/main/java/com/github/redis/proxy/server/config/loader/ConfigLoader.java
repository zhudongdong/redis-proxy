package com.github.redis.proxy.server.config.loader;

import com.github.redis.proxy.server.config.DataNodes;
import com.github.redis.proxy.server.exception.ConfigLoadException;

public interface ConfigLoader
{
    DataNodes load() throws ConfigLoadException;
}
