package com.github.redis.proxy.server.executor;

import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.EventType;
import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;

public class ExceptionExecutor implements Executor
{

    public void execute(ExecutorEvent event) throws ProxyException
    {
        FrontExecutorContext context = event.getSource();
        String errorMessage = context.getError().getMessage();
        context.setResult(("-" + errorMessage + "\r\n").getBytes());
        context.ready();
        context.fireEvent(new ExecutorEvent(context, EventType.BACKEND_MERGED));
    }
}
