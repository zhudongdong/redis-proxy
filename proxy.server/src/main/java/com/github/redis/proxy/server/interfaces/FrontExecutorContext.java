package com.github.redis.proxy.server.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.executor.BackendExecutor;
import com.github.redis.proxy.server.executor.ExceptionExecutor;
import com.github.redis.proxy.server.executor.Executors;
import com.github.redis.proxy.server.executor.FrontExecutor;
import com.github.redis.proxy.server.merge.DefaultMerger;
import com.github.redis.proxy.server.net.front.FrontConnection;
import com.github.redis.proxy.server.parse.DefaultParser;
import com.github.redis.proxy.server.parse.ParseInfo;
import com.github.redis.proxy.server.route.DefaultRouter;
import com.github.redis.proxy.server.route.RouteInfo;

@Accessors(chain = true)
public class FrontExecutorContext
{
    @Getter
    @Setter
    private byte[] commandBytes;
    @Getter
    @Setter
    private byte[] result;
    @Getter
    @Setter
    private ParseInfo parseInfo;
    @Getter
    @Setter
    private RouteInfo routeInfo;
    @Getter
    private List<BackendExecutorContext> childContextList = new ArrayList<>();
    @Setter
    @Getter
    private List<Executor> executors;
    @Getter
    @Setter
    private FrontConnection frontConnection;
    @Getter@Setter
    private boolean isRead;
    @Getter
    @Setter
    private Throwable error;
    private AtomicBoolean ready = new AtomicBoolean();

    public FrontExecutorContext()
    {
    }

    public void fireEvent(ExecutorEvent event)
    {
        switch (event.getType())
        {
            case FRONT_RECEIVED:
                Executors.get(DefaultParser.class).execute(event);
                break;
            case FRONT_PARSED:
                Executors.get(DefaultRouter.class).execute(event);
                break;
            case FRONT_ROUTED:
                Executors.get(BackendExecutor.class).execute(event);
                break;
            case BACKEND_RECEIVED:
                Executors.get(DefaultMerger.class).execute(event);
                break;
            case BACKEND_MERGED:
                Executors.get(FrontExecutor.class).execute(event);
                break;
            case ERROR:
                Executors.get(ExceptionExecutor.class).execute(event);
                break;
                 
            default:
                throw new ProxyException("unsupport event type:" + event.getType());
        }
    }

    public void addChild(BackendExecutorContext child)
    {
        this.getChildContextList().add(child);
    }

    public void ready()
    {
        this.ready.set(true);
    }

    public boolean isReady()
    {
        return ready.get();
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + "@" + hashCode();
    }
}
