package com.github.redis.proxy.server.interfaces;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.redis.proxy.server.config.DataNode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class BackendExecutorContext
{
    @Getter
    private final FrontExecutorContext parent;
    @Getter
    @Setter
    private byte[] result;

    @Getter
    @Setter
    private DataNode dataNode;
    private AtomicBoolean ready = new AtomicBoolean();

    public BackendExecutorContext(FrontExecutorContext parent)
    {
        this.parent = parent;
    }

    public boolean isReady()
    {
        return ready.get();
    }

    public void ready()
    {
        this.ready.set(true);
        List<BackendExecutorContext> childContextList = parent.getChildContextList();
        for (BackendExecutorContext backendExecutorContext : childContextList)
        {
            if (!backendExecutorContext.isReady())
            {
                return;
            }
        }
        parent.fireEvent(new ExecutorEvent(parent, EventType.BACKEND_RECEIVED));
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + "@" + hashCode();
    }
}
