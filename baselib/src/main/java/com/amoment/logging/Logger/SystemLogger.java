package com.amoment.logging.Logger;

/**
 * Created by xudeng on 2017/6/8.
 */
public class SystemLoggner extends BaseLogger{

    private static SystemLoggner m_instance = new SystemLoggner();

    public static SystemLoggner Instance()
    {
        return m_instance;
    }

    private SystemLoggner(){}

    public void fatal(String skdf, String text) {
        try {
            super.fatal(text);

        }catch (Exception e) {

        }

    }
}
