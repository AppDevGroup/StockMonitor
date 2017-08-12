package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.infoplat.IStockInfoProvider;

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
    private List<StockRuntimeInfo> stockMarketInfos = new ArrayList<>();
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

    public void SetStockInfoProvider(IStockInfoProvider stockInfoProvider)
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

    public void AddMonitorCode(String code)
    {
        if(queryCodeList.contains(code))
        {
            LogUtils.Log(code+" already in query list");
            return;
        }

        queryCodeList.add(code);
    }

    private void ProcessNewStockInfo(List<StockRuntimeInfo> infoList)
    {
        if(infoList == null)
        {
            return;
        }

        int i,j;
        boolean bIsNew = true;
        StockRuntimeInfo newMarketInfo;
        infoLock.lock();
        for(i=0; i<infoList.size(); ++i)
        {
            bIsNew =true;
            newMarketInfo = infoList.get(i);
            for(j=0; j<stockMarketInfos.size(); ++j)
            {
                if(stockMarketInfos.get(j).code.equals(newMarketInfo.code))
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

    public StockRuntimeInfo GetStockMarketInfoByCode(String code)
    {
        int i;
        StockRuntimeInfo stockMarketInfo = null;
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
        } catch (Exception ex)
        {
            //            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
