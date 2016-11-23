package com.wly.common;

/**
 * Created by Administrator on 2016/11/22.
 */
public class Utils
{
    static  public  void Log(Object obj)
    {
        System.out.println(obj);
    }
    static  public  void LogException(Exception ex)
    {
        System.out.println(ex.getMessage());
        System.out.println(ex.getStackTrace());
    }
}
