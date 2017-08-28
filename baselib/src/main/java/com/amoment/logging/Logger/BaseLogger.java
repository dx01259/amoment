package com.derivative.Logging.Logger;

import com.derivative.base.core.BaseFunc;
import com.derivative.base.core.ResourceManager;
import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

/**
 * Created by xudeng on 2017/6/10.
 */
public class BaseLogger {

    enum LOGGER_TYPE{
        LOGGER_CONSOLE,
        LOGGER_SOCKET,
        LOGGER_FILE,
    }

    class FileLogsObject{
        public String m_id;
        public String m_prefix;
        public String m_suffix;
        public String m_path;
        public String m_spanType;
        public boolean m_usingBuffer;
        public int m_bufferSize;
        public String m_encoding;
    }

    //记录日志类型
    LOGGER_TYPE m_loggerType;

    protected Logger logger = Logger.getLogger(this.getClass());

    private Hashtable<String, Level> m_LoggerLevel = new Hashtable<String, Level>();
    private Hashtable<String, Logger> m_LoggerObject = new Hashtable<String, Logger>();
    private Hashtable<String, String> m_FileID2Bind = new Hashtable<String, String>();
    private Hashtable<String, FileLogsObject> m_FileLogs2Object =
            new Hashtable<String, FileLogsObject>();

    public BaseLogger()
    {
        m_LoggerLevel.put("fatal", Level.FATAL);
        m_LoggerLevel.put("error", Level.ERROR);
        m_LoggerLevel.put("warn", Level.WARN);
        m_LoggerLevel.put("info", Level.INFO);
        m_LoggerLevel.put("debug", Level.DEBUG);
    }

    public boolean Init(Node logNode, Node fileLogs) throws Exception
    {
        try {
            String sysLevel =  logNode.selectSingleNode("level").getText();
            SetLoggerLevel(sysLevel);
            SetAppender(logNode, sysLevel, fileLogs);
            return true;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private void SetLoggerLevel(String level)
    {
        logger.setLevel(m_LoggerLevel.get(level.toLowerCase()));
    }

    private void SetAppender(Node logNode, String level, Node fileLogs) throws Exception
    {
        String appender = logNode.selectSingleNode("appender").getText();
        Appender appender1 = null;
        char bApp = appender.toLowerCase().charAt(0);
        switch (bApp)
        {
            case 'c':
            default: {
                    m_loggerType = LOGGER_TYPE.LOGGER_CONSOLE;
                    appender1 = new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS} %m%n"));
                    appender1.setName(this.getClass().toString());
                    logger.addAppender(appender1);
                }
                break;
            case 's': {
                    m_loggerType = LOGGER_TYPE.LOGGER_SOCKET;
                    String[] address = logNode.selectSingleNode("address").getText().split(":");
                    String host = address[0];
                    int port = Integer.valueOf(address[1]);
                    appender1 = new SocketAppender(host, port);
                    appender1.setName(this.getClass().toString());
                    logger.addAppender(appender1);
                }
                break;
            case 'f': {
                    m_loggerType = LOGGER_TYPE.LOGGER_FILE;
                    InitFileLogger(logNode, level, fileLogs);
                }
                break;
        }
    }

    private Logger CreateLoggerByLevel(String level, Layout layout, String filePath, String datePattern) throws Exception
    {
        DailyRollingFileAppender appender = new DailyRollingFileAppender();
        FileLogsObject fileBindObject = m_FileLogs2Object.get(m_FileID2Bind.get(level));
        if (fileBindObject == null) {
            throw new Exception("The log instance is not defineed  :" + level);
        }
        Logger log = Logger.getLogger(this.getClass() + "." + level);
        log.setLevel(m_LoggerLevel.get(level.toLowerCase()));
        String file = filePath + (fileBindObject.m_path + File.separator + fileBindObject.m_prefix + fileBindObject.m_suffix);
        appender.setName(this.getClass().toString() + "." + level);
        appender.setEncoding(fileBindObject.m_encoding);
        appender.setLayout(layout);
        appender.setFile(file, true, fileBindObject.m_usingBuffer, fileBindObject.m_bufferSize*1024/*KB*/);
        appender.setDatePattern(datePattern);
        appender.activateOptions();
        log.addAppender(appender);

        return log;
    }

    private void InitFileLogger(Node fileNode, String level, Node fileLogs) throws Exception {

        InitFileBinds(fileNode, fileLogs);
        Layout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS} %m%n");
        String filePath = ResourceManager.GetSysDataroot() + "log" + File.separator;
        String datePattern = "'_'yyyy-MM-dd";
        switch (level.toLowerCase().charAt(0))
        {
            case 'd': {
                Logger log = CreateLoggerByLevel("debug", layout, filePath, datePattern);
                m_LoggerObject.put("debug", log);
            }
            case 'i' : {
                Logger log = CreateLoggerByLevel("info", layout, filePath, datePattern);
                m_LoggerObject.put("info", log);
            }
            case 'w': {
                Logger log = CreateLoggerByLevel("warn", layout, filePath, datePattern);
                m_LoggerObject.put("warn", log);
            }
            case 'e': {
                Logger log = CreateLoggerByLevel("error", layout, filePath, datePattern);
                m_LoggerObject.put("error", log);
            }

            case 'f': {
                Logger log = CreateLoggerByLevel("fatal", layout, filePath, datePattern);
                m_LoggerObject.put("fatal", log);
                break;
            }
            default:{
                throw new Exception("Illegal level of log !");
            }
        }
    }

    private void InitFileBinds(Node fileBind, Node fileLogs) throws Exception
    {
        List fileIdNodes = fileBind.selectSingleNode("file-bind").selectNodes("type");
        for (Object node: fileIdNodes
             ) {
            Element element = (Element)node;
            String attID = element.attributeValue("id");
            if (BaseFunc.isNullOrEmpty(attID)) {
                throw new Exception("loggers/" + fileBind.getName() + "/file-bind/id can not be empty");
            }
            if (m_FileID2Bind.contains(attID)){
                throw new Exception("duplicate file-bind of type is defined :" + attID);
            }
            m_FileID2Bind.put(attID, element.attributeValue("file"));
        }

        List fileLogsNodes = fileLogs.selectNodes("file");

        for (Object node: fileLogsNodes
             ) {
            Element element = (Element)node;
            FileLogsObject fbObj = new FileLogsObject();
            fbObj.m_id = element.attributeValue("id");
            if (BaseFunc.isNullOrEmpty(fbObj.m_id)) {
                throw new Exception("file-logs/file/id can not be empty");
            }
            if (m_FileLogs2Object.contains(fbObj.m_id)){
                throw new Exception("duplicate file-bind of file is defined :" + fbObj.m_id);
            }
            fbObj.m_prefix = element.attributeValue("prefix").trim();
            if (BaseFunc.isNullOrEmpty(fbObj.m_prefix)){
                throw new Exception("file-logs/file/id can not be empty" + fbObj.m_prefix);
            }
            fbObj.m_suffix = element.attributeValue("suffix").trim();
            fbObj.m_path = element.attributeValue("path").trim();
            fbObj.m_spanType = element.attributeValue("span-type").trim();
            fbObj.m_usingBuffer =  element.attributeValue("using-buffer").trim().toUpperCase().equals("N") ? false:true;
            fbObj.m_bufferSize = Integer.valueOf(element.attributeValue("buffer-size").trim());
            fbObj.m_encoding = element.attributeValue("encoding").trim();
            m_FileLogs2Object.put(fbObj.m_id, fbObj);
        }

    }

    protected void fatal(String text) throws Exception {
        if (m_loggerType == LOGGER_TYPE.LOGGER_FILE){
            m_LoggerObject.get("fatal").fatal(text);
        }else {
            logger.fatal(text);
        }
    }

    protected void error(String text) throws Exception {
        if (m_loggerType.equals(LOGGER_TYPE.LOGGER_FILE)){
            m_LoggerObject.get("error").error(text);
        }else {
            logger.error(text);
        }
    }

    protected void warn(String text) throws Exception {
        if (m_loggerType.equals(LOGGER_TYPE.LOGGER_FILE)) {
            m_LoggerObject.get("warn").warn(text);
        }else {
            logger.warn(text);
        }
    }

    protected void info(String text) throws Exception {
        if (m_loggerType.equals(LOGGER_TYPE.LOGGER_FILE)) {
            m_LoggerObject.get("info").info(text);
        }else {
            logger.info(text);
        }
    }

    protected void debug(String text) throws Exception {
        if (m_loggerType.equals(LOGGER_TYPE.LOGGER_FILE)) {
            m_LoggerObject.get("debug").debug(text);
        }else {
            logger.debug(text);
        }
    }
}
