package com.wly.common;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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
            sb.append("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            sb.append("response length:" + responseString.length());
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
}
