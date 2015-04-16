package com.github.redis.proxy.client;

import java.util.HashMap;
import java.util.Map;

import com.github.redis.proxy.client.executor.RandomReadExecutor;
import com.github.redis.proxy.client.executor.ReadBackExecutor;
import com.github.redis.proxy.client.executor.WriteOnlyExecutor;
import com.github.redis.proxy.client.executor.WriteReadSeparationExecutor;

public class Executors
{
    private static final Executors INSTANCE = new Executors();
    private final Map<Class<?>, Executor> executorMap = new HashMap<>();

    private Executors()
    {
        WriteOnlyExecutor writeExecutor = new WriteOnlyExecutor();
        RandomReadExecutor readExecutor = new RandomReadExecutor();
        ReadBackExecutor readBackExecutor = new ReadBackExecutor(writeExecutor, readExecutor);
        WriteReadSeparationExecutor writeReadSpliExecutor = new WriteReadSeparationExecutor(writeExecutor, readExecutor);
        executorMap.put(WriteOnlyExecutor.class, writeExecutor);
        executorMap.put(ReadBackExecutor.class, readBackExecutor);
        executorMap.put(WriteReadSeparationExecutor.class, writeReadSpliExecutor);
    }

    public static Executors instance()
    {
        return INSTANCE;
    }

    public WriteOnlyExecutor writeOnly()
    {
        return (WriteOnlyExecutor) executorMap.get(WriteOnlyExecutor.class);
    }

    public ReadBackExecutor readBack()
    {
        return (ReadBackExecutor) executorMap.get(ReadBackExecutor.class);
    }

    public WriteReadSeparationExecutor writeReadSeparation()
    {
        return (WriteReadSeparationExecutor) executorMap.get(WriteReadSeparationExecutor.class);
    }
}
