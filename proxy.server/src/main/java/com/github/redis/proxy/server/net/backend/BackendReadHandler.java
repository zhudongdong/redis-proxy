package com.github.redis.proxy.server.net.backend;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.BackendExecutorContext;
import com.github.redis.proxy.server.protocol.Command;

public class BackendReadHandler implements CompletionHandler<Integer, BackendConnection>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendReadHandler.class);
    private List<Byte> bytes = new ArrayList<>();
    private byte prefix;
    private int bulkSize;
    private int receivedLineSize;
    private boolean readHead;// 用来定位第一次读取

    public void completed(Integer result, BackendConnection connection)
    {
        if (result <= 0)
        {
            closeConnection(connection);
        } else if (result > 0)
        {
            ByteBuffer buf = connection.getReadBuffer();
            while (load(buf))// 如果读取到完整数据，就处理一次context
            {
                BackendExecutorContext currentContext = connection.getOut().poll();
                setUpContext(currentContext);
                clear();// 重置handler
                if (hasNextData(buf))// 如果读取到了下一次请求的数据，就继续处理，否则跳出循环再次读取
                {
                    prepareForNextRead(buf);
                } else
                {
                    break;
                }
            }
            connection.read();
        }
    }

    private boolean load(ByteBuffer buf)
    {
        buf.flip();
        read(buf);
        if (done())
        {
            return true;
        }
        return false;
    }

    private void clear()
    {
        bytes.clear();
        prefix = 0;
        bulkSize = 0;
        receivedLineSize = 0;
        readHead = false;
    }

    private void prepareForNextRead(ByteBuffer buf)
    {
        // int limit = buf.limit() - buf.position();
        buf.compact();
        // buf.limit(limit);
    }

    private boolean hasNextData(ByteBuffer buf)
    {
        return buf != null && buf.hasRemaining();
    }

    private void setUpContext(BackendExecutorContext currentContext)
    {
        byte[] byteArr = new byte[bytes.size()];
        for (int i = 0; i < byteArr.length; i++)
        {
            byteArr[i] = bytes.get(i);
        }
        currentContext.setResult(byteArr).ready();
    }

    private void closeConnection(BackendConnection connection)
    {
        try
        {
            connection.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void read(ByteBuffer buf)
    {
        if (!readHead && isEnoughData(buf))// 如果是第一次读取数据，并且获取的数据包含\r\n
        {
            prefix = buf.get(0);
            if (prefix == Command.ASTERISK_BYTE)// 如果是*，解析出bulkSize
            {
                bulkSize = bulkSize(buf);
            }
            readHead = true;
        }
        for (int i = 0; i < buf.limit(); i++)
        {
            bytes.add(buf.get(i));// 把数据载入 list
            if (i <= buf.limit() - 2 && buf.get(i) == '\r' && buf.get(i + 1) == '\n')// 是否包含换行符
            {
                receivedLineSize++;
                if (done())// 读完了
                {
                    bytes.add(buf.get(i + 1));
                    buf.position(i + 2);
                    break;
                }
            }
        }
    }

    private int bulkSize(ByteBuffer buf)
    {
        for (int i = 1; i < buf.limit(); i++)
        {
            if (buf.get(i) == '\r')
            {
                if (buf.get(i + 1) == '\n')
                {
                    List<Byte> lengthByteList = new ArrayList<>();
                    for (int j = 1; j < i; j++)
                    {
                        lengthByteList.add(buf.get(j));
                    }
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

    private boolean isEnoughData(ByteBuffer buf)
    {
        for (int i = 0; i < buf.limit(); i++)
        {
            if (i <= buf.limit() - 2 && buf.get(i) == '\r' && buf.get(i + 1) == '\n')
            {
                return true;
            }
        }
        return false;
    }

    private boolean done()
    {
        switch (prefix)
        {
            case Command.ASTERISK_BYTE:
                return bulkSize == (receivedLineSize - 1) / 2;
            case Command.DOLLAR_BYTE:
                if (bytes.get(1) == 45 && bytes.get(2) == 49)// $-1\r\n
                {
                    return receivedLineSize == 1;
                }
                return receivedLineSize == 2;
            case Command.COLON_BYTE:
            case Command.MINUS_BYTE:
            case Command.PLUS_BYTE:
                return receivedLineSize == 1;
            default:
                throw new ProxyException("Invalid Command.");
        }
    }

    public void failed(Throwable e, BackendConnection connection)
    {
        LOGGER.info("backend connection is closed for unknown reason.please check the redis server or network.", e);
        closeConnection(connection);
    }
}
