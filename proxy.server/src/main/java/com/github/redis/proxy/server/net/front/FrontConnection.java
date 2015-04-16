package com.github.redis.proxy.server.net.front;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.interfaces.FrontExecutorContext;

public class FrontConnection
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontConnection.class);
    private FrontReadHandler readHandler = new FrontReadHandler();
    private FrontWriteHandler writeHandler = new FrontWriteHandler();
    private AtomicBoolean isWriting = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    @Getter
    private AsynchronousSocketChannel channel;
    @Getter
    @Setter
    private volatile ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    @Getter
    @Setter
    private volatile ByteBuffer writeBuffer;
    @Getter
    private final ConcurrentLinkedQueue<FrontExecutorContext> in = new ConcurrentLinkedQueue<FrontExecutorContext>();
    @Getter
    private final ConcurrentLinkedQueue<FrontExecutorContext> out = new ConcurrentLinkedQueue<>();

    public FrontConnection(AsynchronousSocketChannel channel)
    {
        this.channel = channel;
    }

    public boolean isClosed()
    {
        return closed.get();
    }

    public boolean writing()
    {
        return this.isWriting.get();
    }

    public void writing(boolean writing)
    {
        this.isWriting.set(writing);
    }

    public void close()
    {
        closed.set(true);
        out.clear();
        in.clear();
        try
        {
            channel.close();
        } catch (IOException e)
        {
            LOGGER.info("failed to close the connection,bmybe the client has close this connection already.");
        }
    }

    public boolean isOpen()
    {
        return channel.isOpen();
    }

    public void read()
    {
        if (isClosed())
        {
            return;
        }
        readBuffer.clear();
        channel.read(readBuffer, this, readHandler);
    }

    @Getter
    private AtomicInteger counter = new AtomicInteger();

    public void write(FrontExecutorContext context)
    {
        if (isClosed())
        {
            return;
        }
        nextWirte();
    }

    public void nextWirte()
    {
        try
        {
            if (this.isWriting.compareAndSet(false, true))
            {
                FrontExecutorContext context = in.poll();
                while (!isClosed())
                {
                    if (context.isReady())// 等待成功
                    {
                        break;
                    }
                }
                this.writeBuffer = ByteBuffer.wrap(context.getResult());
                channel.write(writeBuffer, this, writeHandler);
            }

        } catch (Exception e)
        {
            close();
        }
    }
}
