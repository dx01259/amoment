package com.amoment.logging.Logger;

import com.amoment.util.BaseFunc;
import com.amoment.util.ResourceManager;
import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by xudeng on 2017/6/10.
 */
public class BaseLogger {

    enum LoggerType {
        CONSOLE,
        SOCKET,
        FILE;
    }

    enum LoggerLevel {
        FATAL,ERROR,WARN,INFO,DEBUG;
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
    LoggerType m_loggerType;

    protected Logger logger = Logger.getLogger(this.getClass());

    private Hashtable<String, Level> m_LoggerLevel = new Hashtable<>();
    private Hashtable<String, Logger> m_LoggerObject = new Hashtable<>();
    private Hashtable<String, String> m_FileID2Bind = new Hashtable<>();
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
        String strAppender = logNode.selectSingleNode("appender").getText();
        Appender appender = null;
        switch (LoggerType.valueOf(strAppender.toUpperCase()))
        {
            case CONSOLE:
            default: {
                    m_loggerType = LoggerType.CONSOLE;
                    appender = new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS} %m%n"));
                    appender.setName(this.getClass().toString());
                    logger.addAppender(appender);
                }
                break;
            case SOCKET: {
                    m_loggerType = LoggerType.SOCKET;
                    String[] address = logNode.selectSingleNode("address").getText().split(":");
                    String host = address[0];
                    int port = Integer.valueOf(address[1]);
                    appender = new SocketAppender(host, port);
                    appender.setName(this.getClass().toString());
                    logger.addAppender(appender);
                }
                break;
            case FILE: {
                    m_loggerType = LoggerType.FILE;
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
            throw new Exception("The log instance is not defined  :" + level);
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
        String filePath = ResourceManager.getUserDirectory() + "log" + File.separator;
        String datePattern = "'_'yyyy-MM-dd";
        switch (LoggerLevel.valueOf(level.toUpperCase()))
        {
            case DEBUG: {
                Logger log = CreateLoggerByLevel("debug", layout, filePath, datePattern);
                m_LoggerObject.put("debug", log);
            }
            case INFO: {
                Logger log = CreateLoggerByLevel("info", layout, filePath, datePattern);
                m_LoggerObject.put("info", log);
            }
            case WARN: {
                Logger log = CreateLoggerByLevel("warn", layout, filePath, datePattern);
                m_LoggerObject.put("warn", log);
            }
            case ERROR: {
                Logger log = CreateLoggerByLevel("error", layout, filePath, datePattern);
                m_LoggerObject.put("error", log);
            }

            case FATAL: {
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
        if (m_loggerType == LoggerType.FILE){
            m_LoggerObject.get("fatal").fatal(text);
        }else {
            logger.fatal(text);
        }
    }

    protected void error(String text) throws Exception {
        if (m_loggerType.equals(LoggerType.FILE)){
            m_LoggerObject.get("error").error(text);
        }else {
            logger.error(text);
        }
    }

    protected void warn(String text) throws Exception {
        if (m_loggerType.equals(LoggerType.FILE)) {
            m_LoggerObject.get("warn").warn(text);
        }else {
            logger.warn(text);
        }
    }

    protected void info(String text) throws Exception {
        if (m_loggerType.equals(LoggerType.FILE)) {
            m_LoggerObject.get("info").info(text);
        }else {
            logger.info(text);
        }
    }

    protected void debug(String text) throws Exception {
        if (m_loggerType.equals(LoggerType.FILE)) {
            m_LoggerObject.get("debug").debug(text);
        }else {
            logger.debug(text);
        }
    }
}
