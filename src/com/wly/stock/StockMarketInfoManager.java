package com.wly.stock;

import com.wly.stock.common.StockPriceMonitor;

import java.util.*;

/**
 * Created by Administrator on 2017/2/10.
 */
public class StockMarketInfoManager extends TimerTask
{
    private IStockInfoProvider infoProvider;
    private ArrayList<String> queryCodeList = new ArrayList<>();
    private List<StockPriceMonitor> stockPriceMonitors = new ArrayList<>();

    private static StockMarketInfoManager sInstance = null;
    public static StockMarketInfoManager GetInstance()
    {
        if(sInstance == null)
        {
            sInstance = new StockMarketInfoManager();
        }

        return sInstance;
    }

    public void StockInfoProvider(IStockInfoProvider stockInfoProvider)
    {
        infoProvider = stockInfoProvider;
    }

    public void Start()
    {
        Timer timer = new Timer();
        timer.schedule(this, 0, 1000);
    }

    public void AddMonitor(StockPriceMonitor stockPriceMonitor)
    {
        stockPriceMonitors.add(stockPriceMonitor);
        queryCodeList.add(stockPriceMonitor.code);
    }

    private void ProcessNewStockInfo(List<StockMarketInfo> infoList)
    {
        int i,j;
        for(i=0; i<infoList.size(); ++i)
        {
            for(j=0; j< stockPriceMonitors.size(); ++j)
            {
                if(infoList.get(i).code .equals(stockPriceMonitors.get(j).code))
                {
                    stockPriceMonitors.get(j).OnNewPirce(infoList.get(i));
                }
            }
        }
    }

    public void run()
    {
        try
        {
            ProcessNewStockInfo(infoProvider.GetStockInfoByCode(queryCodeList));
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
