package com.wly.stock.service;

import com.wly.common.LogUtils;
import com.wly.stock.StockContext;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.infoplat.IStockInfoProvider;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuly on 2017/6/25.
 */
public class    ServiceStockRuntimeInfo extends TimerTask
{
    private IStockInfoProvider stockInfoProvider;

    private Lock lockQueryInfo = new ReentrantLock();
    private HashMap<String, Integer> hmQueryCode = new HashMap<>();

    private HashMap<String, StockRuntimeInfo> hmStockRuntimeInfo = new HashMap<>();
    private Lock lockStockRuntimeInfo = new ReentrantLock();

    public ServiceStockRuntimeInfo(IStockInfoProvider stockInfoProvider)
    {
        this.stockInfoProvider = stockInfoProvider;
    }

    private Timer timer;
    public void Start()
    {
        if(timer == null)
        {
            timer = new Timer();
        }
        timer.schedule(this, 0, 1000);
    }

    public void Stop()
    {
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
        }
    }

    public void AddQueryCode(String code)
    {
        int count = 1;
        lockQueryInfo.lock();
        if(hmQueryCode.containsKey(code))
        {
            count = hmQueryCode.get(code)+1;
        }
        hmQueryCode.put(code, count);
        lockQueryInfo.unlock();
    }

    public void RemoveQueryCode(String code)
    {
        lockQueryInfo.lock();
        if(hmQueryCode.containsKey(code))
        {
            int count = hmQueryCode.get(code);
            if(count == 1)
            {
                hmQueryCode.remove(code);
            }
            else
            {
                hmQueryCode.put(code, count-1);
            }
        }
        lockQueryInfo.unlock();
    }

    public StockRuntimeInfo GetStockRuntimeInfoByCode(String code)
    {
        StockRuntimeInfo stockRuntimeInfo = null;

        lockStockRuntimeInfo.lock();
        if(hmStockRuntimeInfo.containsKey(code))
        {
            stockRuntimeInfo = hmStockRuntimeInfo.get(code);
        }

        lockStockRuntimeInfo.unlock();
        return stockRuntimeInfo;
    }

    public void run()
    {
        lockQueryInfo.lock();
        Set<String> querySet = hmStockRuntimeInfo.keySet();
        lockQueryInfo.unlock();

        try
        {
            lockStockRuntimeInfo.lock();

            hmStockRuntimeInfo.clear();
            if(querySet.size() == 0)
            {
                return;
            }

            ArrayList<StockRuntimeInfo> runtimeInfoList = stockInfoProvider.GetStockInfoByCode(new ArrayList<>(querySet));
            int i;
            StockRuntimeInfo stockRuntimeInfo;
            for(i=0; i<runtimeInfoList.size(); ++i)
            {
                stockRuntimeInfo = runtimeInfoList.get(i);
                hmStockRuntimeInfo.put(stockRuntimeInfo.code, stockRuntimeInfo);
            }

            StockContext.GetInstance().GetServiceStockStrategy().OnTick();
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            lockStockRuntimeInfo.unlock();
        }
    }
}
