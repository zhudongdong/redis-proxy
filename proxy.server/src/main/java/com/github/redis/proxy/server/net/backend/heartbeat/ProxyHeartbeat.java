package com.github.redis.proxy.server.net.backend.heartbeat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyHeartbeat implements Heartbeat
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyHeartbeat.class);
    private static final int TIME_OUT = 2000;
    private static int retries = 3;
    private final byte[] ping = new byte[] { 36, 52, 13, 10, 80, 73, 78, 71, 13, 10 };// $4\r\nPING\r\n
    private byte[] pong = new byte[5];// +PONG
    private final String host;
    private final int port;
    private int currentRetry;
    private Socket heartbeatSocket;
    private InputStream heartbeatInputStream;
    private OutputStream heartbeatOutputStream;

    public ProxyHeartbeat(String host, int port) throws IOException
    {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() throws IOException
    {
        heartbeatSocket = new Socket(host, port);
        heartbeatSocket.setKeepAlive(true);
        heartbeatSocket.setReuseAddress(true);
        heartbeatSocket.setKeepAlive(true);
        heartbeatSocket.setTcpNoDelay(true);
        heartbeatSocket.setSoLinger(true, 0);
        heartbeatSocket.setSoTimeout(TIME_OUT);
        heartbeatInputStream = heartbeatSocket.getInputStream();
        heartbeatOutputStream = heartbeatSocket.getOutputStream();
    }

    public boolean heartbeat()
    {
        if (heartbeatSocket == null || heartbeatSocket.isClosed())
        {
            try
            {
                connect();
            } catch (IOException e)
            {
                return false;
            }
        }
        try
        {
            heartbeatOutputStream.write(ping);
            heartbeatInputStream.read(pong);
            currentRetry = 0;
            return true;
        } catch (IOException e)
        {
            // 可能是timeout，这时需要重试，3次之后依然timeout，则认为后端不可用
            if (currentRetry++ >= retries)
            {
                currentRetry = 0;
                return false;
            }
            clear();
            return heartbeat();
        }
    }

    private void clear()
    {
        try
        {
            pong = new byte[5];
            heartbeatSocket.close();
            heartbeatInputStream.close();
            heartbeatOutputStream.close();
        } catch (IOException e)
        {
            LOGGER.info("backend heartbeat socket close exception.", e);
        }
    }
}
