package com.wly.stock.service;

import com.wly.stock.common.IOrderStatMonitor;
import com.wly.stock.common.OrderInfo;

import java.util.ArrayList;

/**
 * Created by wuly on 2017/6/26.
 */
public class ServiceStockTrade implements IOrderStatMonitor
{
    private static ServiceStockTrade sInstance = null;
    public static ServiceStockTrade GetInstance()
    {
        if(sInstance == null)
        {
            sInstance = new ServiceStockTrade();
        }

        return sInstance;
    }

    private ArrayList<OrderInfo> orderInfoList = new ArrayList<>();

    @Override
    public void OnOrderRequest(OrderInfo orderInfo, String platOrderId)
    {

    }

    @Override
    public void OnStockStat(String orderPlatId, int orderStat)
    {

    }
}
