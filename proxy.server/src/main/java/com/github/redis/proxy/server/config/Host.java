package com.github.redis.proxy.server.config;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Host
{
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    @JSONField
    private WriteHost writeHost;
    @Getter
    @Setter
    @JSONField
    private List<ReadHost> readHosts = new ArrayList<>();
    @Getter
    @Setter
    private String maxIdle;
    @Getter
    @Setter
    private String maxTotal;
    @Getter
    @Setter
    private String minIdle;
    @Getter
    @Setter
    private String strategy = "writeOnly";

    public void addReadHost(ReadHost readHost)
    {
        this.readHosts.add(readHost);
    }

    public String toString()
    {
        return "Host [name=" + name + ", writeHost=" + writeHost + ", readHosts=" + readHosts + "]";
    }

}
