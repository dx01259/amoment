package com.amoment.util;

import java.io.File;
import java.io.InputStream;

/**
 * Created by xudeng on 2017/6/8.
 */
public class ResourceManager {

    private static String userDir = null;

    public static String getUserDirectory()
    {
        if (BaseFunc.isNullOrEmpty(userDir)) {
            userDir = System.getProperty("user.dir") + File.separator;
        } else if (!userDir.endsWith(File.separator)) {
            userDir += File.separator;
        }

        return userDir;
    }

    public InputStream getClassDirectoryFile(Class pathClass, String fileDirectory) {
        return pathClass.getResourceAsStream(fileDirectory);
    }
}
