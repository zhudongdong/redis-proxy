package com.github.redis.proxy.server.config;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public class WriteHost extends AbstractHost
{
    public String toString()
    {
        return "WriteHost [host=" + host + ", port=" + port + "]";
    }
}
