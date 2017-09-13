package com.amoment.logging.logger;

/**
 * Created by xudeng on 2017/6/8.
 */
public class CustomLogger extends BaseLogger{

    private CustomLogger(){}

    private static CustomLogger instance = new CustomLogger();

    public static CustomLogger Instance() { return instance; }
}
