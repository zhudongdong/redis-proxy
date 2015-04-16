package com.github.redis.proxy.server.net.front;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontWriteHandler implements CompletionHandler<Integer, FrontConnection>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontWriteHandler.class);

    public void completed(Integer result, FrontConnection connection)
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
                // 写完了尝试写下一次
                connection.writing(false);
                if (!connection.isClosed() && !connection.getIn().isEmpty())
                {
                    connection.nextWirte();
                }
            }
        }
    }

    private boolean hasMoreData(ByteBuffer buffer)
    {
        return buffer != null && buffer.hasRemaining();
    }

    private void closeConnection(FrontConnection connection)
    {
        connection.close();
    }

    public void failed(Throwable e, FrontConnection connection)
    {
        LOGGER.info("backend connection is closed for unknown reason.please check the redis server or network.", e);
        closeConnection(connection);
    }

}
