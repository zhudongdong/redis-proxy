package com.github.redis.proxy.server.config.loader;

import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.github.redis.proxy.server.config.Server;
import com.github.redis.proxy.server.exception.ConfigLoadException;

public class ServerConfigLoader
{
    public Server load(InputStream in) throws ConfigLoadException
    {
        SAXReader reader = new SAXReader();
        try
        {
            Document doc = reader.read(in);
            Element rootElement = doc.getRootElement();
            Server server = newServer(rootElement);
            return server;
        } catch (DocumentException e)
        {
            throw new ConfigLoadException(e);
        }
    }

    private Server newServer(Element rootElement)
    {
        Server server = new Server();
        server.setPort(Integer.parseInt(rootElement.attributeValue("port")));
        server.setConfigLocation(rootElement.attributeValue("configLocation"));
        server.setConfigType(rootElement.attributeValue("configType"));
        return server;
    }
}
