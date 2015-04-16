package com.github.redis.proxy.server.net.backend;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

import com.github.redis.proxy.server.exception.ConnectionException;
import com.github.redis.proxy.server.interfaces.BackendExecutorContext;

public class BackendConnection
{
    private BackendReadHandler readHandler = new BackendReadHandler();
    private BackendWriteHandler writeHandler = new BackendWriteHandler();
    @Getter
    private AsynchronousSocketChannel channel;
    @Getter
    @Setter
    private volatile ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    @Getter
    @Setter
    private volatile ByteBuffer writeBuffer;
    @Getter
    private final ConcurrentLinkedQueue<BackendExecutorContext> in = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<BackendExecutorContext> out = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean closed = new AtomicBoolean();

    private final AtomicBoolean isWriting = new AtomicBoolean();
    
    private final BackendConnectionPool pool;
    public BackendConnection(AsynchronousSocketChannel channel,BackendConnectionPool pool)
    {
        this.channel = channel;
        this.pool = pool;
    }

    public boolean isClosed()
    {
        return closed.get();
    }

    public void close() throws IOException
    {
        if (closed.compareAndSet(false, true))
        {
            in.clear();
            out.clear();
            channel.close();
            pool.recycle(this);
        }
    }

    public boolean isOpen()
    {
        return channel.isOpen();
    }

    public void read()
    {
        readBuffer.clear();
        channel.read(readBuffer, this, readHandler);
    }

    public void write(BackendExecutorContext context)
    {
        if (isClosed())
        {
            throw new ConnectionException("backend connection has bean closed,please try again.");
        }
        synchronized (this)
        {
            this.out.offer(context);
            this.in.offer(context);
        }
        nextWrite();
    }

    public void nextWrite()
    {
        if (this.isWriting.compareAndSet(false, true))
        {
            BackendExecutorContext current = in.poll();
            if (current == null)
            {
                isWriting.set(false);
                return;
            }
            this.writeBuffer = ByteBuffer.wrap(current.getParent().getCommandBytes());
            channel.write(writeBuffer, this, writeHandler);
        }
    }

    public void writing(boolean writing)
    {
        this.isWriting.set(writing);
    }
}
