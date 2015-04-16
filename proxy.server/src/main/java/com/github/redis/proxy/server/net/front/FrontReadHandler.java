package com.github.redis.proxy.server.net.front;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.EventType;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;
import com.github.redis.proxy.server.protocol.Command;

public class FrontReadHandler implements CompletionHandler<Integer, FrontConnection>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontReadHandler.class);
    private List<Byte> bytes = new ArrayList<>();
    private byte prefix;
    private int bulkSize;
    private int receivedLineSize;
    private int readTimes;// 用来定位第一次读取

    public void completed(Integer result, FrontConnection connection)
    {
        if (result <= 0)
        {
            closeConnection(connection);
        } else if (result > 0)
        {
            ByteBuffer buf = connection.getReadBuffer();
            buf.flip();// 准备读取
            buf.limit(result);// 根据result设置limit,这是因为，对buf写入后，他的limit可能并未发生改变
            loadData(buf);
            if (!done())
            {
                connection.read();
            } else
            {
                // 将收到的请求 发送至后端
                FrontExecutorContext context = new FrontExecutorContext().setFrontConnection(connection);
                connection.getIn().offer(context);
                setUpContext(context);
                context.fireEvent(new ExecutorEvent(context, EventType.FRONT_RECEIVED));
                clear();
                // 继续读
                connection.read();
                LOGGER.debug("[FrontReadHandler.{}]收到请求：{}", context, context.getCommandBytes());
            }
        }
    }

    private void clear()
    {
        bytes.clear();
        prefix = 0;
        bulkSize = 0;
        receivedLineSize = 0;
        readTimes = 0;
    }

    private void closeConnection(FrontConnection connection)
    {
        connection.close();
    }

    private void setUpContext(FrontExecutorContext context)
    {
        byte[] byteArr = new byte[bytes.size()];

        for (int i = 0; i < byteArr.length; i++)
        {
            byteArr[i] = bytes.get(i);
        }
        context.setCommandBytes(byteArr);
    }

    private void loadData(ByteBuffer buf)
    {
        for (int i = 0; i < buf.limit(); i++)// buf的clear不会清除数据
        {
            bytes.add(buf.get(i));// 把数据载入 list
            if (i <= buf.limit() - 2 && buf.get(i) == '\r' && buf.get(i + 1) == '\n')// 是否包含换行符
            {
                receivedLineSize++;
            }
        }
        if (readTimes == 0 && isEnoughData())// 如果是第一次读取数据，并且获取的数据包含\r\n
        {
            prefix = bytes.get(0);
            if (prefix == Command.ASTERISK_BYTE)// 如果是*，解析出bulkSize
            {
                bulkSize = bulkSize();
                readTimes++;
            }
        }
    }

    private boolean isEnoughData()
    {
        for (int i = 0; i < bytes.size(); i++)
        {
            if (i <= bytes.size() - 2 && bytes.get(i) == '\r' && bytes.get(i + 1) == '\n')
            {
                return true;
            }
        }
        return false;
    }

    private int bulkSize()
    {
        for (int i = 1; i < bytes.size(); i++)
        {
            if (bytes.get(i) == '\r')
            {
                if (bytes.get(i + 1) == '\n')
                {

                    List<Byte> lengthByteList = bytes.subList(1, i);
                    byte[] lengthByte = new byte[lengthByteList.size()];
                    for (int j = 0; j < lengthByteList.size(); j++)
                    {
                        lengthByte[j] = lengthByteList.get(j);
                    }
                    return Integer.parseInt(new String(lengthByte));
                }
            }
        }
        throw new ProxyException("can not get bulk length info from bytes");
    }

    private boolean done()
    {
        switch (prefix)
        {
            case 0:
                return false;
            case Command.ASTERISK_BYTE:
                return bulkSize == (receivedLineSize - 1) / 2;
            case Command.COLON_BYTE:
            case Command.MINUS_BYTE:
            case Command.PLUS_BYTE:
                return receivedLineSize == 1;
            default:
                throw new ProxyException("Invalid Command.");
        }
    }

    public void failed(Throwable e, FrontConnection connection)
    {
        LOGGER.info("client close the connection.");
        closeConnection(connection);
    }

}
