package com.wly.common;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by Administrator on 2017/2/22.
 */
public class LogUtils
{
    public static int LOG_CONSOLE   = 0;
    public static int LOG_REALTIME  = 1;
    public static int LOG_TRADE     = 2;
    public static int LOG_DB        = 3;
    public static int LOG_MAX       = 4;
    private static final String[] LogNames = {"console", "realtime", "trade", "db"};
    private static Logger[] Loggers = new Logger[LOG_MAX];

    public static void Init(String configProperties)
    {
        PropertyConfigurator.configure(configProperties);

        int i;
        for(i=0; i<LOG_MAX; ++i)
        {
            Loggers[i] = Logger.getLogger(LogNames[i]);
        }
    }

    public static Logger GetLogger(int idx)
    {
        if(idx >= LOG_MAX || idx < 0)
        {
            return null;
        }
        return Loggers[idx];
    }

    public static void Log(String msg)
    {
        GetLogger(LOG_CONSOLE).info(msg);
    }

    public static void LogRealtime(String msg)
    {
        GetLogger(LOG_REALTIME).info(msg);
    }

    public static void LogTrade(String msg)
    {
        GetLogger(LOG_TRADE).info(msg);
    }

    public static void LogDB(String msg)
    {
        GetLogger(LOG_DB).info(msg);
    }
}
