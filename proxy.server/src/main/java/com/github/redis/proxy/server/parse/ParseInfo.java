package com.github.redis.proxy.server.parse;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ParseInfo
{
    @Setter@Getter
    private String command;
    @Setter@Getter
    private String key;
    @Setter@Getter
    private List<String> args;
    @Setter@Getter
    private List<String> commandLines = new ArrayList<>();
}
