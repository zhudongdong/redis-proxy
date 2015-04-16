package com.github.redis.proxy.server.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.merge.DefaultMerger;
import com.github.redis.proxy.server.parse.DefaultParser;
import com.github.redis.proxy.server.route.DefaultRouter;

public class Executors
{
    private static final Map<Class<? extends Executor>, Executor> executorMap = new ConcurrentHashMap<>();

    static
    {
        executorMap.put(DefaultParser.class, new DefaultParser());
        executorMap.put(DefaultRouter.class, new DefaultRouter());
        executorMap.put(BackendExecutor.class, new BackendExecutor());
        executorMap.put(DefaultMerger.class, new DefaultMerger());
        executorMap.put(FrontExecutor.class, new FrontExecutor());
        executorMap.put(ExceptionExecutor.class, new ExceptionExecutor());
    }

    public static <T> Executor get(Class<T> clazz)
    {
        return executorMap.get(clazz);
    }
}
