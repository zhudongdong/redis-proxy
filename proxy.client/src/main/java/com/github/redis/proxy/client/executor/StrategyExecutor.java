package com.github.redis.proxy.client.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.Executor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.client.Executors;
import com.github.redis.proxy.client.merge.DefaultMerger;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.exception.ProxyException;

public class StrategyExecutor extends AbstractExecutor
{
    private Executors executors = Executors.instance();
    private Map<String, Executable> executorMap = new HashMap<>();
    private Executor next;

    public StrategyExecutor()
    {
        next = new DefaultMerger();
        executorMap.put("writeOnly", executors.writeOnly());
        executorMap.put("writeReadSeparation", executors.writeReadSeparation());
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        List<DataNode> dataNodes = context.getDataNodes();
        // TODO 这里先做同步的，有异步的需求再做异步的
        for (DataNode dataNode : dataNodes)
        {
            Executable executor = getExecutor(dataNode.getHost());
            executor.invoke(context, dataNode);
        }
        //TODO 如果是异步处理，这里需要做等待。
        next.doChain(context);
    }

    private Executable getExecutor(Host host)
    {
        return executorMap.get(host.getStrategy());
    }
}
