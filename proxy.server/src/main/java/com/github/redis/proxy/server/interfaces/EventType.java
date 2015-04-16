package com.github.redis.proxy.server.interfaces;

public enum EventType
{
    FRONT_RECEIVED, FRONT_PARSED, FRONT_ROUTED, BACKEND_SENDED, BACKEND_RECEIVED, BACKEND_MERGED, FRONT_SENDED, ERROR
}
