package com.github.redis.proxy.client.executor;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.exception.ProxyException;

/**
 * 先走读节点，没有结果再走写节点
 * 
 * @author Administrator
 *
 */
public class ReadBackExecutor extends AbstractExecutor
{
    private WriteOnlyExecutor writeExecutor;

    private RandomReadExecutor readExecutor;

    public ReadBackExecutor(WriteOnlyExecutor writeExecutor, RandomReadExecutor readExecutor)
    {
        this.writeExecutor = writeExecutor;
        this.readExecutor = readExecutor;
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        readExecutor.doChain(context);
        if (context.getFinalResult() == null)
        {
            writeExecutor.doChain(context);
        }
    }

    public void invoke(ExecutorContext context, DataNode dataNode)
    {
        readExecutor.invoke(context, dataNode);
        if (context.getFinalResult() == null)
        {
            writeExecutor.invoke(context, dataNode);
        }
    }
}
