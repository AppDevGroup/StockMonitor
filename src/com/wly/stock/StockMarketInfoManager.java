package com.wly.stock;

import com.wly.stock.common.StockPriceMonitor;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/2/10.
 */
public class StockMarketInfoManager extends TimerTask
{
    private IStockInfoProvider infoProvider;
    private ArrayList<String> queryCodeList = new ArrayList<>();
    private List<StockMarketInfo> stockMarketInfos = new ArrayList<>();
    private Lock infoLock = new ReentrantLock();
    private boolean hasInited = false;

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

    public boolean GetHasInited()
    {
        return hasInited;
    }

    public void Start()
    {
        Timer timer = new Timer();
        timer.schedule(this, 0, 1000);
    }

//    public void AddMonitor(StockPriceMonitor stockPriceMonitor)
//    {
//        stockPriceMonitors.add(stockPriceMonitor);
//        if(!queryCodeList.contains(stockPriceMonitor.code))
//        {
//            queryCodeList.add(stockPriceMonitor.code);
//        }
//        else
//        {
//            System.out.println(stockPriceMonitor.code+" already in query list");
//        }
//    }

    public void AddMonitorCode(String code)
    {
        if(queryCodeList.contains(code))
        {
            System.out.println(code+" already in query list");
            return;
        }

        queryCodeList.add(code);
    }

    private void ProcessNewStockInfo(List<StockMarketInfo> infoList)
    {
        int i,j;
        boolean bIsNew = true;
        StockMarketInfo newMarketInfo;
        infoLock.lock();
        for(i=0; i<infoList.size(); ++i)
        {
            bIsNew =true;
            newMarketInfo = infoList.get(i);
            for(j=0; j<stockMarketInfos.size(); ++j)
            {
                if(stockMarketInfos.get(j).code == newMarketInfo.code)
                {
                    bIsNew =false;
                    stockMarketInfos.get(j).CopyFrom(newMarketInfo);
                }
            }

            if(bIsNew)
            {
                stockMarketInfos.add(newMarketInfo);
            }
        }
        infoLock.unlock();

        if(!hasInited)
        {
            hasInited =true;
        }
    }

    public  StockMarketInfo GetStockMarketInfoByCode(String code)
    {
        int i;
        StockMarketInfo stockMarketInfo = null;
        for(i=0; i<stockMarketInfos.size(); ++i)
        {
            if(stockMarketInfos.get(i).code.equals(code))
            {
                stockMarketInfo = stockMarketInfos.get(i);
                break;
            }
        }

        return  stockMarketInfo;
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
