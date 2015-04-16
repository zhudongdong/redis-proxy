package com.github.redis.proxy.client.executor;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.server.config.DataNode;

/**
 * 读写分离的executor
 * 
 * @author 朱冬冬
 *
 */
public class WriteReadSeparationExecutor extends AbstractExecutor
{
    private WriteOnlyExecutor writeExecutor;

    private RandomReadExecutor readExecutor;

    public WriteReadSeparationExecutor(WriteOnlyExecutor writeExecutor, RandomReadExecutor readExecutor)
    {
        this.writeExecutor = writeExecutor;
        this.readExecutor = readExecutor;
    }

    public void invoke(ExecutorContext context, DataNode dataNode)
    {
        if (context.isWrite())// 到写节点
        {
            writeExecutor.invoke(context, dataNode);
        } else
        // 到读节点
        {
            readExecutor.invoke(context, dataNode);
        }
    }

}
