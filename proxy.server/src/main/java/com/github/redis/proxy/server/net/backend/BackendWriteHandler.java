package com.github.redis.proxy.server.net.backend;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendWriteHandler implements CompletionHandler<Integer, BackendConnection>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendWriteHandler.class);
    public void completed(Integer result, BackendConnection connection)
    {
        if (result < 0)// 连接关闭
        {
            closeConnection(connection);
        } else
        {
            // 写成功，判断是否写完
            ByteBuffer buffer = connection.getWriteBuffer();
            if (hasMoreData(buffer))// 没写完,继续写
            {
                buffer.compact();
                buffer.flip();
                connection.getChannel().write(buffer, connection, this);
            } else
            {
                connection.writing(false);
                // 写完了尝试写下一次
                if (!connection.isClosed())
                {
                    connection.nextWrite();
                }
            }
        }
    }

    private boolean hasMoreData(ByteBuffer buffer)
    {
        return buffer != null && buffer.hasRemaining();
    }

    private void closeConnection(BackendConnection connection)
    {
        try
        {
            connection.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void failed(Throwable e, BackendConnection connection)
    {
        LOGGER.info("backend connection is closed for unknown reason.please check the redis server or network.",e);
        closeConnection(connection);
    }

}
