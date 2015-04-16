package com.github.redis.proxy.server.interfaces;

import com.github.redis.proxy.server.exception.ParseException;
import com.github.redis.proxy.server.parse.ParseInfo;

public interface Parser
{
    ParseInfo parse(byte [] commandBytes) throws ParseException;
}
