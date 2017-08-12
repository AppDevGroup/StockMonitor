package com.wly.stock;

import com.wly.stock.infoplat.sina.StockInfoProviderSina;
import com.wly.stock.service.ServiceStockRuntimeInfo;
import com.wly.stock.service.ServiceStockStrategy;
import com.wly.stock.service.ServiceStockTrade;

import java.util.concurrent.Executors;

/**
 * Created by wuly on 2017/6/26.
 */
public class MainStockTrade
{
    public static void main(String[] args)
    {
        StockContext stockContext = StockContext.GetInstance();
        stockContext.SetExecutorService(Executors.newCachedThreadPool());
        stockContext.SetServiceStockStrategy(new ServiceStockStrategy());
        stockContext.SetServiceStockRuntimeInfo(new ServiceStockRuntimeInfo(new StockInfoProviderSina()));
        stockContext.SetServiceStockTrade(new ServiceStockTrade());

        stockContext.GetServiceStockRuntimeInfo().Start();
    }
}
