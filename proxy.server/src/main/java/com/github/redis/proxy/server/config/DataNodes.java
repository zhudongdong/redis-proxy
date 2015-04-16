package com.github.redis.proxy.server.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.redis.proxy.server.exception.ProxyException;

import lombok.Getter;
import lombok.Setter;

public class DataNodes
{
    @Getter
    @Setter
    private List<DataNode> dataNodes = new ArrayList<>();

    @Getter
    @Setter
    private List<Host> hosts = new ArrayList<>();

    public Host getHost(String name)
    {
        if (name == null)
        {
            throw new ProxyException("name 不能为空");
        }
        for (Host host : hosts)
        {
            if (host.getName().equals(name))
            {
                return host;
            }
        }
        throw new ProxyException("找不到host:" + name);
    }

    public DataNode getNode(String nodeId)
    {
        for (DataNode dataNode : dataNodes)
        {
            if (dataNode.getId().equals(nodeId))
            {
                return dataNode;
            }
        }
        return null;
    }

    public void addNode(DataNode dataNode)
    {
        this.dataNodes.add(dataNode);
    }

    public void addHost(Host host)
    {
        this.hosts.add(host);
    }

    public void validate()
    {
        Collections.sort(dataNodes, new Comparator<DataNode>()
        {
            public int compare(DataNode o1, DataNode o2)
            {
                if (o1.getRange().getLow() > o2.getRange().getHigh())
                {
                    return 1;
                }
                if (o1.getRange().getHigh() < o2.getRange().getLow())
                {
                    return -1;
                }
                throw new ProxyException("请检查data node 的range是否正确有序");
            }
        });
        for (int i = 0; i < dataNodes.size(); i++)
        {
            if (i == 0)
            {
                DataNode dataNode = dataNodes.get(i);
                if (dataNode.getRange().getLow() != 0)
                {
                    throw new ProxyException("请检查data node 的range是否正确有序");
                }
                continue;
            }
            if (i == dataNodes.size() - 1)
            {
                DataNode dataNode = dataNodes.get(i);
                if (dataNode.getRange().getHigh() != 16383)
                {
                    throw new ProxyException("请检查data node 的range是否正确有序");
                }
                continue;
            }
            if (dataNodes.get(i).getRange().getHigh() + 1 != dataNodes.get(i + 1).getRange().getLow())
            {
                throw new ProxyException("请检查data node 的range是否正确有序");
            }
        }
    }
}
