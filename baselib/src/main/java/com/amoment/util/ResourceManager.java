package com.derivative.base.core;

import java.io.File;

/**
 * Created by xudeng on 2017/6/8.
 */
public class ResourceManager {

    private static String m_SysDataroot = null;

    static {
        m_SysDataroot = System.getProperty("app.dataroot");
        if (null == m_SysDataroot)
        {
            m_SysDataroot = System.getProperty("user.dir") + File.separator + "dataroot"
                    + File.separator;
        }
        else {
            if (!m_SysDataroot.endsWith(File.separator)){
                m_SysDataroot = m_SysDataroot + File.separator;
            }
        }
    }

    public static String GetSysDataroot()
    {
        return m_SysDataroot;
    }

    public static String GetModuleDataRoot(String module)
    {
        return m_SysDataroot + module + File.separator;
    }
}
