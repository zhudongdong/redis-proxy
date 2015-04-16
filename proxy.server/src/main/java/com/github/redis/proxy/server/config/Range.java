package com.github.redis.proxy.server.config;

import java.util.Arrays;
import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Range
{
    @Getter
    @Setter
    private int low;
    @Getter
    @Setter
    private int high;

    public String toString()
    {
        return "Range [low=" + low + ", high=" + high + "]";
    }

    public boolean isEqual(Range r)
    {
        if (this.getHigh() == r.getHigh() && this.getLow() == r.getLow())
        {
            return true;
        }
        return false;
    }

    public boolean isInclude(Range r)
    {
        if (this.getLow() <= r.getLow() && this.getHigh() >= r.getHigh())
        {
            return true;
        }
        return false;
    }

    public boolean composedOf(Range... ranges)
    {
        Arrays.sort(ranges, new RangeComparator());
        int count = 0;
        for (Range range : ranges)
        {
            count += (range.getHigh() - range.getLow());
        }
        if (count == (getHigh() - getLow()))
        {
            return true;
        }
        return false;
    }

    private class RangeComparator implements Comparator<Range>
    {
        public int compare(Range o1, Range o2)
        {
            if (o1.getHigh() <= o2.getLow())
            {
                return -1;
            }
            if (o1.getLow() > o2.getHigh())
            {
                return 1;
            }
            throw new IllegalStateException("不具可比性");
        }

    }
}
