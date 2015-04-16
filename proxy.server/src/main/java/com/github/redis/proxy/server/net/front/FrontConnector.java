package com.github.redis.proxy.server.net.front;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

import com.github.redis.proxy.server.net.ExecutorService;

public class FrontConnector
{
    public void start(int port)
    {
        try
        {
            AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open(AsynchronousChannelGroup
                    .withThreadPool(ExecutorService.instance().newService()));
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, false);
            channel.bind(new InetSocketAddress(port));
            channel.accept(channel, new FrontConnectHandler());
            while (true)
            {
                try
                {
                    Thread.sleep(100000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
