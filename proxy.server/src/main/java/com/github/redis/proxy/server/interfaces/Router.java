package com.github.redis.proxy.server.interfaces;

import com.github.redis.proxy.server.exception.RouteException;
import com.github.redis.proxy.server.parse.ParseInfo;
import com.github.redis.proxy.server.route.RouteInfo;

public interface Router
{
    RouteInfo route(ParseInfo parseInfo) throws RouteException;
}
