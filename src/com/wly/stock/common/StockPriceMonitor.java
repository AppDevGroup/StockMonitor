package com.wly.stock.common;

import com.wly.stock.StockMarketInfo;

/**
 * Created by Administrator on 2017/2/10.
 */
public abstract class StockPriceMonitor
{
    public String code;

    public abstract void OnNewPirce(StockMarketInfo stockMarketInfo);
}
