package com.github.redis.proxy.client.merge;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.server.exception.ProxyException;

public class DefaultMerger extends AbstractExecutor
{
//    private Executor next;

    public DefaultMerger()
    {
//        this.next = ProxyMethodInterceptor.INSTANCE;
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        //TODO 做数据合并的逻辑.
        context.setFinalResult(context.getResultList().get(0));
//        next.doChain(context);
    }

}
