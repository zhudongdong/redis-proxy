package com.github.redis.proxy.server.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redis.proxy.server.exception.ParseException;
import com.github.redis.proxy.server.exception.ProxyException;
import com.github.redis.proxy.server.interfaces.EventType;
import com.github.redis.proxy.server.interfaces.Executor;
import com.github.redis.proxy.server.interfaces.ExecutorEvent;
import com.github.redis.proxy.server.interfaces.FrontExecutorContext;
import com.github.redis.proxy.server.interfaces.Parser;

public class DefaultParser implements Parser, Executor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultParser.class);
    public static final String DOLLAR = "$";
    public static final String ASTERISK = "*";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String COLON = ":";
    public static final String END = "\r\n";
    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';
    public static final byte PLUS_BYTE = '+';
    public static final byte MINUS_BYTE = '-';
    public static final byte COLON_BYTE = ':';

    public void execute(ExecutorEvent event) throws ProxyException
    {
        FrontExecutorContext ctx = event.getSource();
        ParseInfo info = parse(ctx.getCommandBytes());
        ctx.setParseInfo(info).setRead(isRead(info));
        LOGGER.debug("[DefaultParser   .{}]解析成功：{}", ctx, info.getCommandLines());
        ctx.fireEvent(new ExecutorEvent(ctx, EventType.FRONT_PARSED));
    }

    public ParseInfo parse(byte[] commandBytes) throws ParseException
    {
        ParseInfo parseInfo = new ParseInfo();
        String commandStr = new String(commandBytes);
        String[] arr = commandStr.split(END);
        for (String line : arr)
        {
            parseInfo.getCommandLines().add(line);
        }
        parseInfo.setCommand(arr[2]);
        parseInfo.setKey(arr[4]);
        return parseInfo;
    }

    private boolean isRead(ParseInfo info)
    {
        if (info.getCommand().equalsIgnoreCase("set"))
        {
            return true;
        }
        return false;
    }
}
