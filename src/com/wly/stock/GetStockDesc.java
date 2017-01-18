package com.wly.stock;

import com.wly.common.Utils;
import org.apache.log4j.BasicConfigurator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/1/18.
 */
public class GetStockDesc
{
    static public void main(String[] args)
    {
        BasicConfigurator.configure();

        if(args.length == 0)
        {
            Utils.Log("please input stock code");
            return;
        }

        Timer timer = new Timer();
        QueryStockDescInfo task = new QueryStockDescInfo();
        task.codes = args;
        timer.schedule(task, 0, 2000);
    }
}

class QueryStockDescInfo extends TimerTask
{
    public  String[] codes;
    private StockInfoProviderSina provider = new StockInfoProviderSina();
    @Override
    public void run() {

        try {
            int i;
            for(i=0; i<codes.length; ++i)
            {
                Utils.Log(provider.GetStockInfoByCode(codes[i]).toDesc());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}