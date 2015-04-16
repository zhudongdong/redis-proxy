package com.github.redis.proxy.server.interfaces;

import com.github.redis.proxy.server.exception.MergeException;

public interface Merger
{
    void merge(FrontExecutorContext context) throws MergeException;
}
