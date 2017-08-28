package com.amoment.logging.Logger;

/**
 * Created by xudeng on 2017/6/8.
 */
public class SystemLogger extends BaseLogger{

    private static SystemLogger instance = new SystemLogger();

    public static SystemLogger Instance()
    {
        return instance;
    }

    private SystemLogger(){}

    public void fatal(String skdf, String text) {
        try {
            super.fatal(text);

        }catch (Exception e) {

        }

    }
}
