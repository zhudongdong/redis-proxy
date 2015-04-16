package com.github.redis.proxy.server.net;

import java.util.concurrent.Executors;

public class ExecutorService
{
    private static final ExecutorService SERVICE = new ExecutorService();
    private final java.util.concurrent.ExecutorService executor;

    private ExecutorService()
    {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(availableProcessors);
    }

    public java.util.concurrent.ExecutorService newService()
    {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(availableProcessors);
    }

    public java.util.concurrent.ExecutorService defaultService()
    {
        return executor;
    }

    public static ExecutorService instance()
    {
        return SERVICE;
    }

    public java.util.concurrent.ExecutorService heartbeatService()
    {
        return executor;
    }
}
