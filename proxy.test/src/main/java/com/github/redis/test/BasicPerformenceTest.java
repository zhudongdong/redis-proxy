package com.github.redis.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.google.common.collect.Lists;

public class BasicPerformenceTest
{
    public int operateCountPerThread = 1000;

    public String host;

    public int port;

    private JedisPool pool;

    public int threadCount;

    private AtomicInteger wrong = new AtomicInteger();

    public BasicPerformenceTest()
    {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxTotal(200);
//        pool = new JedisPool(config, "172.31.22.84", 6666);
         pool = new JedisPool(config, "localhost", 6666);
    }

    public void outputResultToConsole(List<PerformanceTestResult> insertResultList,
            List<PerformanceTestResult> queryResultList, List<PerformanceTestResult> queryResultList1)
    {
        System.out.println("执行结束，生成测试报告：");
        System.out.println("失败次数:" + wrong.get());
        if (insertResultList != null && insertResultList.size() > 0)
        {
            System.out.println("插入性能测试结果如下:");
            System.out.println("线程数/操作次数	|	TPS		|	平均耗时(ms)	|	最大耗时(ms)		");
            for (PerformanceTestResult performanceTestResult : insertResultList)
            {
                System.out.println(performanceTestResult.getThreadCount() + "/"
                        + performanceTestResult.getOperateCountPerThread() + "		|	" + performanceTestResult.getTps()
                        + "		|	" + performanceTestResult.getAvgCost() + "		|	" + performanceTestResult.getMaxCost());
            }
        }
        // if (queryResultList != null && queryResultList.size() > 0)
        // {
        // System.out.println("查询性能测试结果如下:");
        // System.out.println("查询数据库性能测试结果如下:");
        // System.out.println("线程数/操作次数	|	TPS		|	平均耗时(ms)	|	最大耗时(ms)		");
        // for (PerformanceTestResult performanceTestResult : queryResultList)
        // {
        // System.out.println(performanceTestResult.getThreadCount() + "/"
        // + performanceTestResult.getOperateCountPerThread() + "		|	" +
        // performanceTestResult.getTps()
        // + "		|	" + performanceTestResult.getAvgCost() + "		|	" +
        // performanceTestResult.getMaxCost());
        // }
        // }
        if (queryResultList1 != null && queryResultList1.size() > 0)
        {
            System.out.println("查询缓存性能测试结果如下:");
            System.out.println("线程数/操作次数	|	TPS		|	平均耗时(ms)	|	最大耗时(ms)		");
            for (PerformanceTestResult performanceTestResult : queryResultList1)
            {
                System.out.println(performanceTestResult.getThreadCount() + "/"
                        + performanceTestResult.getOperateCountPerThread() + "		|	" + performanceTestResult.getTps()
                        + "		|	" + performanceTestResult.getAvgCost() + "		|	" + performanceTestResult.getMaxCost());
            }
        }
        try
        {
            outputResultToFile(insertResultList, queryResultList);
        } catch (Exception e)
        {
        }
    }

    public void outputResultToFile(List<PerformanceTestResult> insertResultList,
            List<PerformanceTestResult> queryResultList) throws IOException
    {
        String path = System.getProperty("outPutFile");
        if (path == null || path.length() == 0)
        {
            System.out.println("未设置报告文件路径，不能生成文报告件。");
            return;
        }
        File outputFile = new File(System.getProperty("outPutFile"));
        if (!outputFile.exists())
        {
            outputFile.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(outputFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write("执行结束，生成测试报告：\n");
        writer.write("插入性能测试结果如下:\n");
        writer.write("线程数/操作次数\t|\t\t\tTPS\t\t\t|\t\t平均耗时(ms)\t\t|\t\t最大耗时(ms)	\n");
        for (PerformanceTestResult performanceTestResult : insertResultList)
        {
            writer.write(performanceTestResult.getThreadCount() + "/"
                    + performanceTestResult.getOperateCountPerThread() + "\t\t\t\t\t\t|\t\t"
                    + performanceTestResult.getTps() + "\t\t|\t\t\t" + performanceTestResult.getAvgCost()
                    + "\t\t\t\t\t|\t\t\t\t\t" + performanceTestResult.getMaxCost() + "\n");
        }
        writer.write("查询性能测试结果如下:\n");
        writer.write("线程数/操作次数\t|\t\t\tTPS\t\t\t|\t\t平均耗时(ms)\t\t|\t\t最大耗时(ms)	\n");
        for (PerformanceTestResult performanceTestResult : queryResultList)
        {
            writer.write(performanceTestResult.getThreadCount() + "/"
                    + performanceTestResult.getOperateCountPerThread() + "\t\t\t\t\t\t|\t\t"
                    + performanceTestResult.getTps() + "\t\t|\t\t\t" + performanceTestResult.getAvgCost()
                    + "\t\t\t\t\t|\t\t\t\t\t" + performanceTestResult.getMaxCost() + "\n");
        }
        writer.close();
    }

    public void test() throws InterruptedException
    {
        List<Integer> threadCountList = Lists.newArrayList(threadCount);
        List<PerformanceTestResult> insertResultList = Lists.newArrayList();
        List<PerformanceTestResult> queryResultList = Lists.newArrayList();
        List<PerformanceTestResult> queryResultList1 = Lists.newArrayList();
        for (Integer threadCount : threadCountList)
        {
            System.out.println("开始执行[" + threadCount + "]线程组..");
//            testInsert(threadCount, operateCountPerThread, insertResultList);
            // testQuery(threadCount, operateCountPerThread, queryResultList);
            testQuery(threadCount, operateCountPerThread, queryResultList1);
            System.out.println("[" + threadCount + "]线程组执行结束.");
        }
        outputResultToConsole(insertResultList, queryResultList, queryResultList1);
    }

    public void testQuery(Integer threadCount, int operateCountPerThread, List<PerformanceTestResult> queryResultList)
            throws InterruptedException
    {
        CountDownLatch stopSig = new CountDownLatch(threadCount);
        List<BasicThread> threadList = Lists.newArrayList();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++)
        {
            QueryThread queryThread = new QueryThread(i * operateCountPerThread, stopSig, operateCountPerThread);
            queryThread.start();
            threadList.add(queryThread);
        }
        stopSig.await();
        long end = System.currentTimeMillis();
        double tps = (double) threadCount * operateCountPerThread / (double) (end - begin) * 1000;
        double avgCost = calAvgCost(threadCount * operateCountPerThread, threadList);
        double maxCost = getMaxCost(threadList);
        PerformanceTestResult performanceTestResult = new PerformanceTestResult();
        performanceTestResult.setAvgCost(avgCost).setMaxCost(maxCost).setThreadCount(threadCount)
                .setOperateCountPerThread(operateCountPerThread).setTps(tps);
        queryResultList.add(performanceTestResult);
    }

    public void testInsert(Integer threadCount, int operateCountPerThread, List<PerformanceTestResult> insertResultList)
            throws InterruptedException
    {
        List<BasicThread> threadList = Lists.newArrayList();
        CountDownLatch stopSig = new CountDownLatch(threadCount);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++)
        {
            InsertThread thread = new InsertThread(i * operateCountPerThread, stopSig, operateCountPerThread);
            thread.start();
            threadList.add(thread);
        }
        stopSig.await();
        long end = System.currentTimeMillis();
        double tps = (double) threadCount * operateCountPerThread / (double) (end - begin) * 1000;
        double avgCost = calAvgCost(threadCount * operateCountPerThread, threadList);
        double maxCost = getMaxCost(threadList);
        PerformanceTestResult performanceTestResult = new PerformanceTestResult();
        performanceTestResult.setAvgCost(avgCost).setMaxCost(maxCost).setThreadCount(threadCount)
                .setOperateCountPerThread(operateCountPerThread).setTps(tps);
        insertResultList.add(performanceTestResult);
    }

    private double getMaxCost(List<BasicThread> threadList)
    {
        List<Long> maxCostOfEachThread = Lists.newArrayList();
        for (BasicThread basicThread : threadList)
        {
            List<Long> costPerOperateList = basicThread.getCostPerOperateList();
            Collections.sort(costPerOperateList);
            maxCostOfEachThread.add(costPerOperateList.get(costPerOperateList.size() - 1));
        }
        Collections.sort(maxCostOfEachThread);
        return maxCostOfEachThread.get(maxCostOfEachThread.size() - 1);
    }

    private double calAvgCost(int operateCount, List<BasicThread> threadList)
    {
        long totalCost = 0;
        for (BasicThread basicThread : threadList)
        {
            List<Long> costPerOperateList = basicThread.getCostPerOperateList();
            for (Long costPerOperate : costPerOperateList)
            {
                totalCost += costPerOperate;
            }
        }
        return (double) totalCost / operateCount;
    }

    public int getOperateCountPerThread()
    {
        return this.operateCountPerThread;
    }

    public void setOperateCountPerThread(int operateCountPerThread)
    {
        this.operateCountPerThread = operateCountPerThread;
    }

    public class InsertThread extends BasicThread
    {
        private int index;
        private CountDownLatch stopSig;
        private int count;
        private Jedis jedis;

        public InsertThread(int index, CountDownLatch stopSig, int count)
        {
            this.index = index;
            this.stopSig = stopSig;
            this.count = count;
        }

        public void run()
        {
            for (int j = 0; j < count; j++)
            {
                int current = index++;
                String key = String.valueOf(current);
                String value = String.valueOf(current);
                long begin = System.currentTimeMillis();
                jedis = pool.getResource();
                jedis.set(key, value);
                jedis.close();
                long end = System.currentTimeMillis();
                costPerOperateList.add(end - begin);
            }
            stopSig.countDown();
        }
    }

    public class QueryThread extends BasicThread
    {
        private int index;
        private CountDownLatch stopSig;
        private int count;
        private Jedis jedis;

        public QueryThread(int index, CountDownLatch stopSig, int count)
        {
            this.index = index;
            this.stopSig = stopSig;
            this.count = count;
        }

        public void run()
        {
            for (int j = 0; j < count; j++)
            {
                int current = index++;
                long begin = System.currentTimeMillis();
                jedis = pool.getResource();
                String value = jedis.get("" + current);
                if (!value.equals(("" + current)))
                {
                    throw new RuntimeException("fuck you bitch:[key-"+current+",value-"+value+"]");
                }
                jedis.close();
                long end = System.currentTimeMillis();
                costPerOperateList.add(end - begin);
            }
            stopSig.countDown();
        }
    }

    public class BasicThread extends Thread
    {
        protected List<Long> costPerOperateList = new LinkedList<Long>();

        public List<Long> getCostPerOperateList()
        {
            return this.costPerOperateList;
        }
    }
}
