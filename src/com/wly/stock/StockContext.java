package com.wly.stock;

import com.wly.stock.service.ServiceStockRuntimeInfo;
import com.wly.stock.service.ServiceStockStrategy;
import com.wly.stock.service.ServiceStockTrade;

import java.util.concurrent.ExecutorService;

/**
 * Created by wuly on 2017/6/26.
 */
public class StockContext
{
    static private StockContext s_instace = null;
    static public StockContext GetInstance()
    {
        if(s_instace == null)
        {
            s_instace = new StockContext();
        }
        return  s_instace;
    }

    private StockContext(){}

    private ServiceStockRuntimeInfo serviceStockRuntimeInfo;
    public ServiceStockRuntimeInfo GetServiceStockRuntimeInfo()
    {
        return serviceStockRuntimeInfo;
    }

    public void SetServiceStockRuntimeInfo(ServiceStockRuntimeInfo serviceStockRuntimeInfo)
    {
        this.serviceStockRuntimeInfo = serviceStockRuntimeInfo;
    }

    private ServiceStockTrade serviceStockTrade;
    public ServiceStockTrade GetServiceStockTrade()
    {
        return serviceStockTrade;
    }
    public void SetServiceStockTrade(ServiceStockTrade serviceStockTrade)
    {
        this.serviceStockTrade = serviceStockTrade;
    }

    private ServiceStockStrategy serviceStockStrategy;
    public ServiceStockStrategy GetServiceStockStrategy()
    {
        return serviceStockStrategy;
    }

    public void SetServiceStockStrategy(ServiceStockStrategy serviceStockStrategy)
    {
        this.serviceStockStrategy = serviceStockStrategy;
    }

    private ExecutorService executorService;
    public ExecutorService GetExecutorService()
    {
        return executorService;
    }

    public void SetExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
    }
}
