package com.github.redis.proxy.server.merge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.exception.MergeException;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.EventType;
import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;
import com.github.redis.proxy.server.interfaces.Merger;

public class DefaultMerger implements Executor, Merger
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMerger.class);

    public void execute(ExecutorEvent event) throws ProxyException
    {
        FrontExecutorContext context = event.getSource();
        merge(context);
        context.ready();
        LOGGER.debug("[DefaultMerger   .{}]汇聚成功：[result size = {}]", context, context.getResult().length);
        context.fireEvent(new ExecutorEvent(context, EventType.BACKEND_MERGED));
    }

    public void merge(FrontExecutorContext context) throws MergeException
    {
        //TODO merge logic if nacessery
        context.setResult(context.getChildContextList().get(0).getResult());
    }

}
