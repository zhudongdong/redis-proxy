package com.github.redis.proxy.server.route;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.github.redis.proxy.server.config.DataNode;

@Accessors(chain = true)
public class RouteInfo
{
    @Getter@Setter
    private List<DataNode> dataNodes;
}
