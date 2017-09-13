package com.amoment.logging.logger;

/**
 * Created by xudeng on 2017/6/8.
 */
public class UserLogger extends BaseLogger {

    private static UserLogger instance = new UserLogger();

    public static UserLogger Instance()
    {
        return instance;
    }

    private UserLogger(){}
}
