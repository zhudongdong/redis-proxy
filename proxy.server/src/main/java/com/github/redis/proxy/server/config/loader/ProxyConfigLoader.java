package com.github.redis.proxy.server.config.loader;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.DataNodes;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.config.Range;
import com.github.redis.proxy.server.config.ReadHost;
import com.github.redis.proxy.server.config.WriteHost;
import com.github.redis.proxy.server.exception.ConfigLoadException;

public class ProxyConfigLoader implements ConfigLoader
{
    private String path;

    public ProxyConfigLoader(String path)
    {
        this.path = path;
    }

    public DataNodes load() throws ConfigLoadException
    {
        SAXReader reader = new SAXReader();
        try
        {
            Document doc = reader.read(this.getClass().getResourceAsStream(path));
            Element rootElement = doc.getRootElement();
            DataNodes dataNodes = newDataNodes(rootElement);
            return dataNodes;
        } catch (DocumentException e)
        {
            throw new ConfigLoadException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private DataNodes newDataNodes(Element rootElement)
    {
        DataNodes dataNodes = new DataNodes();

        List hostEles = rootElement.elements("host");

        for (Object object : hostEles)
        {
            Element hostEle = (Element) object;
            dataNodes.getHosts().add(newHost(hostEle));
        }
        List elements = rootElement.elements("dataNode");
        for (Object object : elements)
        {
            Element ele = (Element) object;
            dataNodes.getDataNodes().add(newDataNode(ele, dataNodes));
        }
        return dataNodes;
    }

    @SuppressWarnings("rawtypes")
    private Host newHost(Element hostEle)
    {
        Host host = new Host();
        host.setName(hostEle.attributeValue("name"));
        host.setMaxIdle(hostEle.attributeValue("maxIdle"));
        host.setMaxTotal(hostEle.attributeValue("maxTotal"));
        host.setMinIdle(hostEle.attributeValue("minIdle"));
        String strategy = hostEle.attributeValue("strategy");
        if (strategy != null)
        {
            host.setStrategy(strategy);
        }
        Element writeEle = hostEle.element("writeHost");
        host.setWriteHost(newWriteHost(writeEle));
        List readEles = hostEle.elements("readHost");
        for (Object object : readEles)
        {
            Element readEle = (Element) object;
            host.getReadHosts().add(newReadHost(readEle));
        }
        return host;
    }

    private DataNode newDataNode(Element ele, DataNodes dataNodes)
    {
        DataNode dataNode = new DataNode();
        dataNode.setId(ele.attributeValue("id"));
        Host host = dataNodes.getHost(ele.attributeValue("host"));
        Range range = new Range();
        dataNode.setRange(range);
        String rangeExp = ele.attributeValue("range");
        int low = Integer.parseInt(rangeExp.trim().substring(1, rangeExp.trim().length() - 1).split("-")[0]);
        int high = Integer.parseInt(rangeExp.trim().substring(1, rangeExp.trim().length() - 1).split("-")[1]);
        if (low < 0 || high > 16383)
        {
            throw new ConfigLoadException("slot[最小：0，最大：16383]配置错误，low：" + low + "  high:  " + high);
        }
        range.setLow(low);
        range.setHigh(high);
        dataNode.setHost(host);
        return dataNode;
    }

    private ReadHost newReadHost(Element readHostEle)
    {
        ReadHost readHost = new ReadHost();
        readHost.setHost(readHostEle.attributeValue("host"));
        readHost.setPort(Integer.parseInt(readHostEle.attributeValue("port")));
        return readHost;
    }

    private WriteHost newWriteHost(Element ele)
    {
        WriteHost writeHost = new WriteHost();
        writeHost.setHost(ele.attributeValue("host"));
        writeHost.setPort(Integer.parseInt(ele.attributeValue("port")));
        return writeHost;
    }
}
