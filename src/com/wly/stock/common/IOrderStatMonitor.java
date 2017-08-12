package com.wly.stock.common;

/**
 * Created by wuly on 2017/8/12.
 */
public interface IOrderStatMonitor
{
    void OnOrderRequest(OrderInfo orderInfo, String platOrderId);
    void OnStockStat(String orderPlatId, int orderStat);
}
