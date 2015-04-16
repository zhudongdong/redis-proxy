package com.github.redis.proxy.server.route;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.exception.RouteException;
import com.github.redis.proxy.server.interfaces.EventType;
import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;
import com.github.redis.proxy.server.interfaces.Router;
import com.github.redis.proxy.server.parse.ParseInfo;
import com.github.redis.proxy.server.route.hash.ProxyHashing;

public class DefaultRouter implements Executor, Router
{
    private ProxyHashing hashing = new ProxyHashing();
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRouter.class);

    public void execute(ExecutorEvent event) throws ProxyException
    {
        FrontExecutorContext context = event.getSource();
        context.setRouteInfo(route(context.getParseInfo()));
        List<DataNode> dataNodes = context.getRouteInfo().getDataNodes();
        LOGGER.debug("[DefaultRouter   .{}]路由成功：{}", context, dataNodes);
        context.fireEvent(new ExecutorEvent(context, EventType.FRONT_ROUTED));
    }

    public RouteInfo route(ParseInfo parseInfo) throws RouteException
    {
        String key = parseInfo.getKey();
        DataNode node = ConfigHolder.instance().getNode(hashing.crc32(key) % 16384);
        RouteInfo routeInfo = new RouteInfo();
        List<DataNode> dataNodes = new ArrayList<>();
        dataNodes.add(node);
        routeInfo.setDataNodes(dataNodes);
        return routeInfo;
    }

}
