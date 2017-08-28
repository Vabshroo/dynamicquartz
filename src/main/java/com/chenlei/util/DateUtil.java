package com.chenlei.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenlei on 2017/8/16.
 */
public class DateUtil {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String nowDateStr(){
        return "[" + DATE_FORMAT.format(new Date()) + "] ";
    }

}
