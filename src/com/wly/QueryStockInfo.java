package com.wly;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.StockInfoProviderSina;

import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * Created by Administrator on 2016/11/30.
 */
public class QueryStockInfo
{
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        Timer timer = new Timer();
        if(args.length == 0)
        {
            Utils.Log("please input stock code");
            return;
        }

        QueryStockDetailInfo task = new QueryStockDetailInfo();
        task.code = args[0];
        timer.schedule(task, 0, 2000);
    }
}

class QueryStockDetailInfo extends TimerTask
{
    public  String code;
    private StockInfoProviderSina provider = new StockInfoProviderSina();
    @Override
    public void run() {

        try {
            System.out.println(provider.GetStockInfoByCode(code).toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
