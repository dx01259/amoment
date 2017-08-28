package com.derivative.Logging.Service;

import com.derivative.Logging.Logger.CustomLogger;
import com.derivative.Logging.Logger.SystemLoggner;
import com.derivative.Logging.Logger.UserLogger;
import com.derivative.base.core.ResourceManager;
import com.derivative.base.util.UtilXml;
import org.dom4j.Node;

/**
 * Created by xudeng on 2017/6/8.
 */
public class LogManager {
    private static boolean m_serviceReady = false;//服务器启动标示

    private static final String Config_File_Name = "log-config.xml";

    private static SystemLoggner m_SystemLogger = SystemLoggner.Instance();
    private static UserLogger m_UserLogger = UserLogger.Instance();

    private static CustomLogger m_CustomLogger = CustomLogger.Instance();

    public static void Start() throws Exception
    {
        try
        {
            Init();
            m_serviceReady = true;
        }catch (Exception e)
        {
            throw e;
        }
    }

    private static void Init() throws Exception
    {
        try
        {
            String logFilePath = ResourceManager.GetSysDataroot() + Config_File_Name;
            UtilXml xml = new UtilXml(logFilePath);
            //初始化日志对象
            InitLoggers(xml);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    private static void InitLoggers(UtilXml xmlFile) throws Exception
    {
        try {
            //初始化系统日志
            Node syslogger = xmlFile.getElement("//logging/loggers/system-logger");
            Node fileLogs = xmlFile.getElement("//logging/file-logs");
            m_SystemLogger.Init(syslogger, fileLogs);
            //初始化用户日志
            Node userlogger = xmlFile.getElement("//logging/loggers/user-logger");
            m_UserLogger.Init(userlogger, fileLogs);
            //初始化自定义日志
            Node customlogger = xmlFile.getElement("//logging/loggers/custom-logger");
            m_CustomLogger.Init(customlogger, fileLogs);
        }catch (Exception e){
            throw e;
        }

    }

    public static SystemLoggner getSysLogger() {
        return m_SystemLogger;
    }
    public static UserLogger getUserLogger() {
        return m_UserLogger;
    }
    public static CustomLogger getCustomLogger() {
        return m_CustomLogger;
    }
}
