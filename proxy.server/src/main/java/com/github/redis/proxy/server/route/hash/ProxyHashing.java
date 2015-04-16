package com.github.redis.proxy.server.route.hash;

import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

public class ProxyHashing
{
    public int crc32(String input)
    {
        Preconditions.checkNotNull(input, "哈希方法参数不能为空.");
        return Math.abs(Hashing.crc32c().hashBytes(input.getBytes()).asInt());
    }

    public int mermer(String input)
    {
        Preconditions.checkNotNull(input, "哈希方法参数不能为空.");
        int value = Math.abs(Hashing.crc32().hashBytes(input.getBytes()).asInt());
        return value;
    }

    public int arrayHash(String input)
    {
        Preconditions.checkNotNull(input, "哈希方法参数不能为空.");
        int value = Math.abs(Arrays.hashCode(input.getBytes()));
        return value;
    }
}
