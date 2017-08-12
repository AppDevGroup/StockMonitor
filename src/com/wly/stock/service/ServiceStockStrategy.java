package com.wly.stock.service;

import com.wly.stock.strategy.IStockStrategy;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuly on 2017/6/25.
 */
public class ServiceStockStrategy
{
    private ArrayList<IStockStrategy> stockStrategies = new ArrayList<>();
    private Lock lockStockStrategies = new ReentrantLock();

    public void AddStrategy(IStockStrategy stockStrategy)
    {
        lockStockStrategies.lock();
        stockStrategies.add(stockStrategy);
        lockStockStrategies.unlock();
    }

    public void OnTick()
    {
        lockStockStrategies.lock();
        int i;
        for(i=0; i<stockStrategies.size(); ++i)
        {
            stockStrategies.get(i).OnTick();
        }
        lockStockStrategies.unlock();
    }
}
