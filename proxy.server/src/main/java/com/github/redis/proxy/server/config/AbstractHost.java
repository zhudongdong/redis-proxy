package com.github.redis.proxy.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public abstract class AbstractHost
{
    @Getter
    @Setter
    public String host;
    @Getter
    @Setter
    public int port;

    public String toString()
    {
        return "AbstractHost [host=" + host + ", port=" + port + "]";
    }

}
