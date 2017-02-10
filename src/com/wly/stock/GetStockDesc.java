package com.wly.stock;

import com.wly.common.Utils;
import com.wly.stock.common.StockPriceMonitor;

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
            Utils.Log("please input stock code");
            return;
        }

        StockMarketInfoManager stockMarketInfoManager = StockMarketInfoManager.GetInstance();
        stockMarketInfoManager.StockInfoProvider(new StockInfoProviderSina());
        int i;
        for(i=0; i<args.length; ++i)
        {
            stockMarketInfoManager.AddMonitor(new StockPriceMonitorDesc(args[i]));
        }

        stockMarketInfoManager.Start();
    }
}

class StockPriceMonitorDesc extends StockPriceMonitor
{
    public StockPriceMonitorDesc(String code)
    {
        this.code = code;
    }

    @Override
    public void OnNewPirce(StockMarketInfo stockMarketInfo)
    {
        Utils.Log(stockMarketInfo.toDesc());
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