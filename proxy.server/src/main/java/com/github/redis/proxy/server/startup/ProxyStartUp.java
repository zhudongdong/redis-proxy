package com.github.redis.proxy.server.startup;

import com.github.redis.proxy.server.config.ConfigHolder;
import com.github.redis.proxy.server.config.Server;
import com.github.redis.proxy.server.net.backend.BackendConnectionFactory;
import com.github.redis.proxy.server.net.front.FrontConnector;

public class ProxyStartUp
{
    public static void main(String[] args)
    {
        //加载配置文件
        Server server = ConfigHolder.instance().getServer();
        BackendConnectionFactory.instance();
        FrontConnector connector = new FrontConnector();
        //启动
        connector.start(server.getPort());
    }
}
