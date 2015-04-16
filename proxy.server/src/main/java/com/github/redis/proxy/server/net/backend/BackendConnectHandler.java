package com.github.redis.proxy.server.net.backend;

import java.nio.channels.CompletionHandler;

public class BackendConnectHandler implements CompletionHandler<Void, BackendConnection>
{

    public void completed(Void result, BackendConnection connection)
    {
        connection.read();
    }

    public void failed(Throwable exc, BackendConnection attachment)
    {
        exc.printStackTrace();

    }

}
