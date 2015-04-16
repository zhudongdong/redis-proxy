package com.github.redis.proxy.client.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.github.redis.proxy.client.AbstractExecutor;
import com.github.redis.proxy.client.Executor;
import com.github.redis.proxy.client.ExecutorContext;
import com.github.redis.proxy.client.parse.DefaultParser;
import com.github.redis.proxy.server.exception.ProxyException;

public class ProxyMethodInterceptor extends AbstractExecutor implements MethodInterceptor
{
    public static final ProxyMethodInterceptor INSTANCE = new ProxyMethodInterceptor();
    private Executor next;

    private ProxyMethodInterceptor()
    {
        this.next = new DefaultParser();
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable
    {
        // TODO 根据method 过滤掉不支持的方法
        if (holder.isReloading())
        {
            throw new ProxyException("配置文件正在重载..");
        }
        ExecutorContext context = new ExecutorContext().setMethod(method).setArgs(args);
        next.doChain(context);
        return context.getFinalResult();
    }

    public void doChain(ExecutorContext context) throws ProxyException
    {
        // TODO 调到这里来就说明执行完成了
    }

}
