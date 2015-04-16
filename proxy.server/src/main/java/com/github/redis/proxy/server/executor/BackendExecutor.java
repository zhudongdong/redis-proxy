package com.github.redis.proxy.server.executor;

import java.util.List;

import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.BackendExecutorContext;
import com.github.redis.proxy.server.interfaces.EventType;
import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;

public class BackendExecutor implements Executor
{
    private StrategyExecutors executors = StrategyExecutors.instance();

    public void execute(ExecutorEvent event) throws ProxyException
    {
        FrontExecutorContext context = event.getSource();
        List<DataNode> dataNodes = context.getRouteInfo().getDataNodes();
        for (DataNode dataNode : dataNodes)
        {
            BackendExecutorContext child = new BackendExecutorContext(context).setDataNode(dataNode);
            context.addChild(child);
            try
            {
                getExecutor(dataNode.getHost()).execute(child);
            } catch (Exception e)
            {
                context.setError(e);
                context.fireEvent(new ExecutorEvent(context, EventType.ERROR));
            }
        }
    }

    public StrategyExecutor getExecutor(Host host)
    {
        String strategy = host.getStrategy();
        return executors.getExecutor(strategy);
    }
}
