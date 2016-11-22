package com.wly.stock;

import com.wly.common.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockInfoProviderSina implements IStockInfoProvider
{
    @Override
    public StockInfo GetStockInfoByCode(int code) throws Exception
    {
        String UrlFormat = "http://hq.sinajs.cn/list=sh%d";
        String urlstr = String.format(UrlFormat, code);
        URL url = new URL(urlstr);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "GB2312"));
        String readStr = bufferedReader.readLine();
        System.out.println(readStr);
        String s2 = new String(readStr.getBytes("GBK"),"utf-8");
        return null;
    }

    @Override
    public ArrayList<StockInfo> GetStockInfoByCode(ArrayList<Integer> codeList) throws Exception
    {
        StringBuilder sb = new StringBuilder("http://hq.sinajs.cn/list=");
        int i;
        int code;
        String prefix;
        eStockPlate plate = eStockPlate.None;
        for(i=0; i<codeList.size(); ++i)
        {
            code = codeList.get(i);
            plate = StockUtils.GetPlateByCode(code);
            prefix = null;
            switch (plate)
            {
                case PlateSH:
                    prefix = "sh";
                    break;
                case PlateSZ:
                    prefix = "sz";
                    break;
            }

            if(prefix == null)
            {
                Utils.Log("ignore unknow stock "+code);
                continue;
            }

            if(i==0)
            {
                sb.append(prefix+code);
            }
            else
            {
                sb.append(","+prefix+code);
            }
        }

        URL url = new URL(sb.toString());
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "GB2312"));
        sb.delete( 0, sb.length() );
        //string
        String strTmp;
        while((strTmp = bufferedReader.readLine()) != null)
        {
            sb.append(strTmp);
            sb.append("\n");
        }
        System.out.println(sb.toString());
        return null;
    }
}
