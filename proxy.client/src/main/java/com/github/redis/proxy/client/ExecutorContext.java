package com.github.redis.proxy.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import redis.clients.jedis.Jedis;

import com.github.redis.proxy.server.config.DataNode;

@Accessors(chain = true)
public class ExecutorContext
{
    @Getter
    @Setter
    private String key;
    @Getter
    @Setter
    private List<DataNode> dataNodes;
    @Getter
    @Setter
    private Method method;
    @Getter
    @Setter
    private Object[] args;
    @Getter
    @Setter
    private List<Object> resultList = new ArrayList<>();
    @Getter
    @Setter
    private Object finalResult;
    @Getter
    @Setter
    private boolean isWrite;

    public void addResult(Object result)
    {
        this.resultList.add(result);
    }

    public void invoke(Jedis jedis)
    {
        try
        {
            resultList.add(method.invoke(jedis, args));
            finalResult = getResultList().get(0);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            e.printStackTrace();
        } finally
        {
            jedis.close();
        }
    }
}
