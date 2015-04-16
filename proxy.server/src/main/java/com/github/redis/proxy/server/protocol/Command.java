package com.github.redis.proxy.server.protocol;

import java.util.List;

import lombok.Setter;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain=true)
public class Command
{
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
    @Setter@Getter
    private String command;
    @Setter@Getter
    private String key;
    @Setter@Getter
    private List<String> args;
    @Setter@Getter
    private List<String> commandLines;
    @Getter@Setter
    private byte [] commandBytes;
    
}
