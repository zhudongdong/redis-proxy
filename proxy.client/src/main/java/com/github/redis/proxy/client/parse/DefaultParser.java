package com.github.redis.proxy.client.parse;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.Executor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.client.route.DefaultRouter;
import com.github.redis.proxy.server.exception.ProxyException;

public class DefaultParser extends AbstractExecutor
{
    private Executor next;

    public DefaultParser()
    {
        this.next = new DefaultRouter();
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        // TODO 这里逻辑需要细化 1 判断读写 2 解析参数
        String name = context.getMethod().getName();
        if (name.startsWith("set"))
        {
            context.setWrite(true);
        }
        context.setKey((String) context.getArgs()[0]);
        next.doChain(context);
    }
}
