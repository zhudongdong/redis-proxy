package com.github.redis.proxy.server.net.pool;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.github.redis.proxy.server.exception.ProxyException;


public class BufferWrapper
{

    private ByteBuffer origin;

    public ByteBuffer slice()
    {
        return origin.slice();
    }

    public ByteBuffer duplicate()
    {
        return origin.duplicate();
    }

    public ByteBuffer asReadOnlyBuffer()
    {
        return origin.asReadOnlyBuffer();
    }

    public byte get()
    {
        return origin.get();
    }

    public ByteBuffer put(byte b)
    {
        return origin.put(b);
    }

    public byte get(int index)
    {
        return origin.get(index);
    }

    public ByteBuffer put(int index, byte b)
    {
        return origin.put(index, b);
    }

    public ByteBuffer compact()
    {
        return origin.compact();
    }

    public boolean isDirect()
    {
        return origin.isDirect();
    }

    byte _get(int i)
    {
        throw new ProxyException("unsupported");
    }

    void _put(int i, byte b)
    {
        throw new ProxyException("unsupported");
    }

    public char getChar()
    {
        return origin.getChar();
    }

    public ByteBuffer putChar(char value)
    {
        return origin.putChar(value);
    }

    public char getChar(int index)
    {
        return origin.getChar(index);
    }

    public ByteBuffer putChar(int index, char value)
    {
        return origin.putChar(index, value);
    }

    public CharBuffer asCharBuffer()
    {
        return origin.asCharBuffer();
    }

    public short getShort()
    {
        return origin.getShort();
    }

    public ByteBuffer putShort(short value)
    {
        return origin.putShort(value);
    }

    public short getShort(int index)
    {
        return origin.getShort(index);
    }

    public ByteBuffer putShort(int index, short value)
    {
        return origin.putShort(index, value);
    }

    public ShortBuffer asShortBuffer()
    {
        return origin.asShortBuffer();
    }

    public int getInt()
    {
        return origin.getInt();
    }

    public ByteBuffer putInt(int value)
    {
        return origin.putInt(value);
    }

    public int getInt(int index)
    {
        return origin.getInt(index);
    }

    public ByteBuffer putInt(int index, int value)
    {
        return origin.putInt(index, value);
    }

    public IntBuffer asIntBuffer()
    {
        return origin.asIntBuffer();
    }

    public long getLong()
    {
        return origin.getLong();
    }

    public ByteBuffer putLong(long value)
    {
        return origin.putLong(value);
    }

    public long getLong(int index)
    {
        return origin.getLong(index);
    }

    public ByteBuffer putLong(int index, long value)
    {
        return origin.putLong(index, value);
    }

    public LongBuffer asLongBuffer()
    {
        return origin.asLongBuffer();
    }

    public float getFloat()
    {
        return origin.getFloat();
    }

    public ByteBuffer putFloat(float value)
    {
        return origin.putFloat(value);
    }

    public float getFloat(int index)
    {
        return origin.getFloat(index);
    }

    public ByteBuffer putFloat(int index, float value)
    {
        return origin.putFloat(index, value);
    }

    public FloatBuffer asFloatBuffer()
    {
        return origin.asFloatBuffer();
    }

    public double getDouble()
    {
        return origin.getDouble();
    }

    public ByteBuffer putDouble(double value)
    {
        return origin.putDouble(value);
    }

    public double getDouble(int index)
    {
        return origin.getDouble(index);
    }

    public ByteBuffer putDouble(int index, double value)
    {
        return origin.putDouble(index, value);
    }

    public DoubleBuffer asDoubleBuffer()
    {
        return origin.asDoubleBuffer();
    }

    public boolean isReadOnly()
    {
        return origin.isReadOnly();
    }
}
