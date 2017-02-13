package com.wly.stock.common;

import com.wly.stock.StockConst;
import com.wly.stock.StockMarketInfo;

/**
 * Created by Administrator on 2017/2/10.
 */
public class StockPriceMonitorTrade extends StockPriceMonitor
{
    public ITradeInterface tradeInterface;
    public OrderInfo orderInfo;

    @Override
    public void OnNewPirce(StockMarketInfo stockMarketInfo)
    {
        //竞价时间 不执行交易
        if(stockMarketInfo.sellInfo.get(0).price == stockMarketInfo.buyInfo.get(0).price)
        {
            return;
        }

        if(orderInfo.GetStat() != OrderInfo.OderStat_Ready)
        {
            return;
        }

        boolean canTrade = false;
        if(orderInfo.tradeFlag == StockConst.TradeBuy && orderInfo.orderPrice>= stockMarketInfo.sellInfo.get(0).price
                && orderInfo.count <= stockMarketInfo.sellInfo.get(0).amount)
        {
            canTrade = true;
        }

        if(orderInfo.tradeFlag == StockConst.TradeSell && orderInfo.orderPrice <= stockMarketInfo.buyInfo.get(0).price
                && orderInfo.count <= stockMarketInfo.buyInfo.get(0).amount)
        {
            canTrade = true;
        }

        if(!canTrade)
        {
            return;
        }

        tradeInterface.DoOrder(orderInfo);
    }
}
