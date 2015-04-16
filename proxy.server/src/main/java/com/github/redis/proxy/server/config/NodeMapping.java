package com.github.redis.proxy.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class NodeMapping
{
    @Getter
    @Setter
    private DataNode dataNode;
    @Getter
    @Setter
    private Range range;

}
