package com.github.redis.test;

public class ProxyTest
{
    public static void main(String[] args) throws InterruptedException
    {
        Integer operateCountPerThread = Integer.parseInt(args[0]);
        BasicPerformenceTest test = new BasicPerformenceTest();
        test.operateCountPerThread = operateCountPerThread;
        test.threadCount = Integer.parseInt(args[1]);
//        test.host = args[1];
//        test.port = Integer.parseInt(args[2]);
        test.test();
//        ProxyClient.main(null);
    }
}
