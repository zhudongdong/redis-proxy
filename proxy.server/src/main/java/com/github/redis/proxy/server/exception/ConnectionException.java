package com.github.redis.proxy.server.exception;

public class ConnectionException extends ProxyException
{

    private static final long serialVersionUID = 1L;

    public ConnectionException()
    {
        super();
    }

    public ConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ConnectionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionException(String message)
    {
        super(message);
    }

    public ConnectionException(Throwable cause)
    {
        super(cause);
    }

}
