package com.github.redis.proxy.client.route;

import java.util.ArrayList;
import java.util.List;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.Executor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.client.executor.StrategyExecutor;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.exception.ProxyException;

public class DefaultRouter extends AbstractExecutor
{
    private Executor next;

    public DefaultRouter()
    {
        this.next = new StrategyExecutor();
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        List<DataNode> dataNodes = new ArrayList<>();
        DataNode dataNode = holder.getNode(hashing.crc32(context.getKey()) % 16384);
        if (!dataNode.isAvaliable())
        {
            throw new ProxyException("当前节点不可用."+dataNode);
        }
        dataNodes.add(dataNode);
        context.setDataNodes(dataNodes);
        next.doChain(context);
    }
}
