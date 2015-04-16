package com.github.redis.server.test;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.github.redis.proxy.server.config.DataNode;
import com.github.redis.proxy.server.config.Host;
import com.github.redis.proxy.server.config.Range;
import com.github.redis.proxy.server.config.ReadHost;
import com.github.redis.proxy.server.config.WriteHost;

public class JSONTest
{
    public static void main(String[] args)
    {
        List<ReadHost> readHosts = new ArrayList<>();
        readHosts.add((ReadHost) new ReadHost().setHost("10.1.1.4").setPort(6666));
        readHosts.add((ReadHost) new ReadHost().setHost("10.2.2.2").setPort(5555));
        WriteHost writeHost = (WriteHost) new WriteHost().setHost("10.2.35.6").setPort(6379);
        Host host = new Host().setMaxIdle("10").setMaxTotal("20").setMinIdle("5").setName("a").setReadHosts(readHosts)
                .setWriteHost(writeHost);
        DataNode dataNode = new DataNode();
        dataNode.setHost(host);
        dataNode.setRange(new Range().setHigh(1000).setLow(555));
        String jsonString = JSONObject.toJSONString(dataNode);
        System.out.println(jsonString);
        JSONObject.parseObject(jsonString, DataNode.class);
    }
}
