package com.github.redis.proxy.client;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.github.redis.proxy.client.parse.DefaultParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

public class JedisAdapter extends Jedis
{
    private Map<String, Method> methodMap = new HashMap<String, Method>();
    private Executor next = new DefaultParser();
    
    public JedisAdapter()
    {
        super("");
        try
        {
            methodMap.put("set", Jedis.class.getDeclaredMethod("set", String.class,String.class));
        } catch (NoSuchMethodException | SecurityException e)
        {
        }
    }
    public JedisAdapter(JedisShardInfo shardInfo)
    {
        super(shardInfo);
    }

    public JedisAdapter(String host, int port, int timeout)
    {
        super(host, port, timeout);
        // TODO Auto-generated constructor stub
    }

    public JedisAdapter(String host, int port)
    {
        super(host, port);
        
    }

    public JedisAdapter(String host)
    {
        super(host);
    }

    public JedisAdapter(URI uri, int timeout)
    {
        super(uri, timeout);
        // TODO Auto-generated constructor stub
    }

    public JedisAdapter(URI uri)
    {
        super(uri);
        // TODO Auto-generated constructor stub
    }

    public String set(String key, String value)
    {
        ExecutorContext context = new ExecutorContext().setArgs(new Object[]{key,value});
        context.setMethod(methodMap.get("set"));
        next.doChain(context);
        return (String) context.getFinalResult();
    }

    public String get(String key)
    {
        return super.get(key);
    }
}
