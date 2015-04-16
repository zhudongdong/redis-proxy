package com.github.redis.test;

import java.text.DecimalFormat;

public class PerformanceTestResult
{

    private static final DecimalFormat FORMAT = new DecimalFormat("#.00");
    private int threadCount;
    private int operateCountPerThread;
    private double tps;
    private double avgCost;
    private double maxCost;
    public int getThreadCount()
    {
        return threadCount;
    }

    public PerformanceTestResult setThreadCount(int threadCount)
    {
        this.threadCount = threadCount;
        return this;
    }

    public int getOperateCountPerThread()
    {
        return operateCountPerThread;
    }

    public PerformanceTestResult setOperateCountPerThread(int operateCountPerThread)
    {
        this.operateCountPerThread = operateCountPerThread;
        return this;
    }

    public double getTps()
    {
        return Double.parseDouble(FORMAT.format(tps));
    }

    public PerformanceTestResult setTps(double tps)
    {
        this.tps = tps;
        return this;
    }

    public double getAvgCost()
    {
        return Double.parseDouble(FORMAT.format(avgCost));
    }

    public PerformanceTestResult setAvgCost(double avgCost)
    {
        this.avgCost = avgCost;
        return this;
    }

    public double getMaxCost()
    {
        return Double.parseDouble(FORMAT.format(maxCost));
    }

    public PerformanceTestResult setMaxCost(double maxCost)
    {
        this.maxCost = maxCost;
        return this;
    }
}
