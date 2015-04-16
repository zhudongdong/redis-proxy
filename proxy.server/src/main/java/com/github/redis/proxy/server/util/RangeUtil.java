package com.github.redis.proxy.server.util;

import com.github.redis.proxy.server.config.Range;

public class RangeUtil
{
    public static boolean isEqual(Range r1, Range r2)
    {
        if (r1.getHigh() == r2.getHigh() && r1.getLow() == r2.getLow())
        {
            return true;
        }
        return false;
    }

    public static boolean isBigger(Range r1, Range r2)
    {
        if (r1.getLow() < r2.getLow() && r1.getHigh() > r2.getHigh())
        {
            return true;
        }
        if (r1.getLow() > r2.getLow() && r1.getHigh() < r2.getHigh())
        {
            return false;
        }
        throw new IllegalStateException("不具可比性");
    }
}
