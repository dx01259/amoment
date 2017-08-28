package com.derivative.Logging.Logger;

import org.dom4j.Node;

/**
 * Created by xudeng on 2017/6/8.
 */
public class CustomLogger extends BaseLogger{

    private CustomLogger(){}

    public static CustomLogger m_instance = new CustomLogger();

    public static CustomLogger Instance() { return m_instance; }
}
