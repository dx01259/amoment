package com.amoment.util;

public class BaseFunc {
    public static boolean isNullOrEmpty(String strValue) {
        return  (null==strValue || "".equals(strValue)) ? true : false;
    }
}
