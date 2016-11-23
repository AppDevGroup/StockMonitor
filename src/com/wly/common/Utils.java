package com.wly.common;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/11/22.
 */
public class Utils
{
    static  public  void Log(Object obj)
    {
        System.out.println(GetTimestampNow().toString()+" "+ obj);
    }
    static  public  void LogException(Exception ex)
    {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        //System.out.println(ex.getStackTrace());
    }

    static public Timestamp GetTimestampNow()
    {
        java.util.Date date=new java.util.Date();
        return  new Timestamp(date.getTime());
    }
}
