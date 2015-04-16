package com.github.redis.server.test;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;

public class HazelCastTest
{
    public static void main(String[] args)
    {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();
        System.out.println(networkConfig);
    }
}
