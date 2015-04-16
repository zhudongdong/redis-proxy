package com.github.redis.proxy.server.interfaces;

import java.util.EventObject;

import lombok.Getter;

public class ExecutorEvent extends EventObject
{

    private static final long serialVersionUID = 1L;

    @Getter
    private final EventType type;

    public ExecutorEvent(FrontExecutorContext source, EventType type)
    {
        super(source);
        this.type = type;
    }

    public FrontExecutorContext getSource()
    {
        return (FrontExecutorContext) super.getSource();
    }
}
