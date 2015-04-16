package com.github.redis.server.test;

import java.util.Scanner;

import redis.clients.jedis.Jedis;

public class JedisClientTest
{
    public static void main(String[] args) throws InterruptedException
    {
        Jedis jedis = new Jedis("localhost", 6666);
//        jedis.set("a", "b");
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            String next = scanner.next();
            try
            {
                String set = jedis.get(next);
                System.out.println(set);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
