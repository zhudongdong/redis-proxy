package com.github.redis.proxy.server.config;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class DataNode
{
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private Host host;
    @Getter
    @Setter
    private Range range;

    private AtomicBoolean isAvaliable = new AtomicBoolean(true);

    public String toString()
    {
        return "DataNode [id=" + id + ", host=" + host + ", range=" + range + "]";
    }

    public boolean isAvaliable()
    {
        return isAvaliable.get();
    }

    public void avaliable(boolean avaliable)
    {
        isAvaliable.set(avaliable);
    }
}
