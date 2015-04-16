package com.github.redis.proxy.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Server
{
    @Getter
    @Setter
    private int port;
    @Getter
    @Setter
    private String configType;
    @Getter
    @Setter
    private String configLocation;
}
