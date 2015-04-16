package com.github.redis.proxy.server.executor;

import java.util.HashMap;
import java.util.Map;

import com.github.redis.proxy.server.exception.ProxyException;
import com.google.common.base.Preconditions;

public class StrategyExecutors
{
    private static final StrategyExecutors INSTANCE = new StrategyExecutors();
    private Map<String, StrategyExecutor> executorMap = new HashMap<String, StrategyExecutor>();

    private StrategyExecutors()
    {
        executorMap.put("writeOnly", new WriteOnlyExecutor());
        executorMap.put("readWriteSeparation", new ReadWriteSeparationExecutor());
    }

    public static StrategyExecutors instance()
    {
        return INSTANCE;
    }

    public StrategyExecutor getExecutor(String strategy)
    {
        Preconditions.checkNotNull(strategy, "strategy can not be null");
        if (executorMap.containsKey(strategy))
        {
            return executorMap.get(strategy);
        }
        throw new ProxyException("can not find StrategyExecutor for strategy:" + strategy);
    }
}
