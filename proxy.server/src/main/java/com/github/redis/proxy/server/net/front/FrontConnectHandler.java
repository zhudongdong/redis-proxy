package com.github.redis.proxy.server.net.front;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;

public class FrontConnectHandler implements
        CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>
{
    private AtomicInteger counter = new AtomicInteger();
    public void completed(AsynchronousSocketChannel clientChannel, AsynchronousServerSocketChannel serverChannel)
    {
        try
        {
            clientChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            clientChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            clientChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        serverChannel.accept(serverChannel, this);
        FrontConnection connection = new FrontConnection(clientChannel);
        System.out.println(counter.incrementAndGet()+":-----"+connection);
        connection.read();
    }

    public void failed(Throwable exc, AsynchronousServerSocketChannel serverChannel)
    {
        exc.printStackTrace();
    }

}
