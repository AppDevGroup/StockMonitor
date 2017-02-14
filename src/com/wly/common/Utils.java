package com.wly.common;

import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.user.UserInfo;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String GetResponseFull(HttpResponse httpResponse)
            throws ParseException, IOException
    {
        StringBuilder sb = new StringBuilder();
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        sb.append("status:" + httpResponse.getStatusLine()+"\n");
        sb.append("headers:\n");

        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            sb.append("\t" + iterator.next()+"\n");
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            sb.append("response length:" + responseString.length()+"\n");
            sb.append("response content:"
                    + responseString.replace("\r\n", ""));
        }

        return sb.toString();
    }

    public static String GetResponseContent(HttpResponse httpResponse)
    {
        try
        {
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity);
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
    }

    public static volatile int IdIndex = 0;
    static public int GetId()
    {
        return ++IdIndex;
    }

    static public String GetDate()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    static public String GetDateTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    static public int GetLastInserId()
    {
        try
        {
            int ret = 0;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync("select LAST_INSERT_ID()");
            ResultSet rs = dbQuery.resultSet;
            while (rs.next())
            {
                ret = rs.getInt(0);
            }
            dbQuery.Close();
            return ret;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }
}
