package com.amoment.logging.service;

import com.amoment.logging.logger.CustomLogger;
import com.amoment.logging.logger.SystemLogger;
import com.amoment.logging.logger.UserLogger;
import com.amoment.util.ResourceManager;
import com.amoment.util.UtilXml;
import org.dom4j.Node;

/**
 * Created by xudeng on 2017/6/8.
 */
public class LogManager {
    private static boolean serviceReady = false;//服务器启动标示

    private static final String Config_File_Name = "log-config.xml";

    private static SystemLogger systemLogger = SystemLogger.Instance();
    private static UserLogger userLogger = UserLogger.Instance();

    private static CustomLogger customLogger = CustomLogger.Instance();

    public static void Start() throws Exception
    {
        try
        {
            Init();
            serviceReady = true;
        }catch (Exception e)
        {
            throw e;
        }
    }

    private static void Init() throws Exception
    {
        try
        {
            String logFilePath = ResourceManager.getUserDirectory() + Config_File_Name;
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
            Node sysLogger = xmlFile.getElement("//logging/loggers/system-logger");
            Node fileLogs = xmlFile.getElement("//logging/file-logs");
            systemLogger.Init(sysLogger, fileLogs);
            //初始化用户日志
            Node userNode = xmlFile.getElement("//logging/loggers/user-logger");
            userLogger.Init(userNode, fileLogs);
            //初始化自定义日志
            Node customNode = xmlFile.getElement("//logging/loggers/custom-logger");
            customLogger.Init(customNode, fileLogs);
        }catch (Exception e){
            throw e;
        }

    }

    public static SystemLogger getSysLogger() {
        return systemLogger;
    }
    public static UserLogger getUserLogger() {
        return userLogger;
    }
    public static CustomLogger getCustomLogger() {
        return customLogger;
    }
}
