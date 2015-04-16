package com.github.redis.proxy.server.interfaces;

import java.util.EventListener;

import com.github.redis.proxy.server.exception.ProxyException;

public interface Executor extends EventListener
{
    void execute(ExecutorEvent event) throws ProxyException;
}

