package com.derivative.Logging.Logger;

/**
 * Created by xudeng on 2017/6/8.
 */
public class UserLogger extends BaseLogger {

    private static UserLogger m_instance = new UserLogger();

    public static UserLogger Instance()
    {
        return m_instance;
    }

    private UserLogger(){};
}
