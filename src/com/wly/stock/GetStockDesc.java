package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.common.StockPriceMonitor;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/1/18.
 */
public class GetStockDesc
{
    static public void main(String[] args)
    {
        if(args.length == 0)
        {
            System.out.println("please input stock code");
            return;
        }

        LogUtils.Init("config/log4j.properties");
        StockMarketInfoManager stockMarketInfoManager = StockMarketInfoManager.GetInstance();
        stockMarketInfoManager.SetStockInfoProvider(new StockInfoProviderSina());
        StockPriceMonitorManager stockPriceMonitorManager = StockPriceMonitorManager.GetInstance();
        int i;
        for(i=0; i<args.length; ++i)
        {
            stockPriceMonitorManager.AddMonitor(new StockPriceMonitorDesc(args[i]));
            stockMarketInfoManager.AddMonitorCode (args[i]);
        }

        stockMarketInfoManager.Start();
        stockPriceMonitorManager.Start();
    }
}

class StockPriceMonitorDesc extends StockPriceMonitor
{
    public StockPriceMonitorDesc(String code)
    {
        this.code = code;
    }

    @Override
    public void OnNewPirce(StockRuntimeInfo stockMarketInfo)
    {
        System.out.println(stockMarketInfo.toDesc());
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