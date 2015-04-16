package com.github.redis.proxy.server.exception;

public class MergeException extends ProxyException
{

    private static final long serialVersionUID = 1L;

    public MergeException()
    {
        super();
    }

    public MergeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MergeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MergeException(String message)
    {
        super(message);
    }

    public MergeException(Throwable cause)
    {
        super(cause);
    }

}
