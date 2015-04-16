package com.github.redis.proxy.server.config;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public class ReadHost extends AbstractHost
{

    public String toString()
    {
        return "ReadHost [host=" + host + ", port=" + port + "]";
    }
}
