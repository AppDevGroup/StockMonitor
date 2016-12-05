package com.wly;

import com.mysql.jdbc.Util;
import com.wly.common.Utils;
import com.wly.stock.StockInfo;
import com.wly.stock.StockInfoProviderSina;
import com.wly.stock.StockUtils;

import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * Created by Administrator on 2016/11/30.
 */
public class QueryStockInfo
{
    static private Logger logger = Logger.getLogger("LogTest");

    static public void main(String[] args)
    {
        BasicConfigurator.configure();

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
            Utils.Log(provider.GetStockInfoByCode(code).toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
