package com.wly.stock.common;

import com.wly.common.LogUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockMarketInfo
{
    public static final int MarketInfoStat_None = 0;
    public static final int MarketInfoStat_Normal = 1;
    public static final int MarketInfoStat_Bidding = 2;
    public static final int MarketInfoStat_Max = 3;
    public static final int MarketInfoStat_Min = 4;

    private int marketInfoStat = MarketInfoStat_None;

    static public  class TradeInfo
    {
        public float price;
        public long amount;
    }

    public String code;
    public String name;
    public float priceInit;
    public float priceLast;
    public float priceNew;
    public float priceMax;
    public float priceMin;
    public float priceBuy;
    public float priceSell;
    public long tradeCount;
    public float tradeMoney;
    public ArrayList<TradeInfo> buyInfo = new ArrayList<TradeInfo>(5);
    public ArrayList<TradeInfo> sellInfo = new ArrayList<TradeInfo>(5);

    public float GetChange()
    {
        return  priceNew- priceLast;
    }

    public float GetRatio()
    {
        return GetChange()/ priceLast *100;
    }

    public  void CopyFrom(StockMarketInfo src)
    {
        this.code = src.code;
        this.name = src.name;
        this.priceInit = src.priceInit;
        this.priceLast = src.priceLast;
        this.priceNew = src.priceNew;
        this.priceMax = src.priceMax;
        this.priceMin = src.priceMin;
        this.priceBuy = src.priceBuy;
        this.priceSell = src.priceSell;
        this.tradeCount = src.tradeCount;
        this.tradeMoney = src.tradeMoney;
        this.marketInfoStat = src.GetMarketInfoStat();
        this.buyInfo.clear();
        this.buyInfo.addAll(src.buyInfo);
        this.sellInfo.clear();
        this.sellInfo.addAll(src.sellInfo);
    }

    public boolean TestDeal(int tradeFlag, float price, int count)
    {
        if(count == 0)
        {
            return false;
        }

        int i;
        TradeInfo tradeInfo;
        boolean ret = false;
        if(tradeFlag == StockConst.TradeSell)
        {
            int num = 0;
            for(i=0; i<buyInfo.size(); ++i)
            {
                tradeInfo = buyInfo.get(i);
                if(tradeInfo.price >= price)
                {
                    num += tradeInfo.amount;
                }
            }

            ret = num >= count;
        }
        else if(tradeFlag == StockConst.TradeBuy)
        {
            int num = 0;
            for(i=0; i<sellInfo.size(); ++i)
            {
                tradeInfo = sellInfo.get(i);
                if(tradeInfo.price <= price)
                {
                    num += tradeInfo.amount;
                }
            }

            ret = num >= count;
        }

        return  ret;
    }

    public void CacuStat()
    {
        TradeInfo maxBuyInfo = buyInfo.get(0);
        float buyPrice = maxBuyInfo.price;
        TradeInfo minSellInfo = sellInfo.get(0);
        float sellPrice = minSellInfo.price;
        if((buyPrice< 0.01f && sellPrice < 0.01f)
        ||(buyPrice == sellPrice))
        {
            marketInfoStat = MarketInfoStat_Bidding;
        }
        else if(buyPrice < 0.01f)
        {
            LogUtils.LogRealtime(String.format("%s is min price %.2f sellCount=%dh", code, sellPrice, (minSellInfo.amount+50)/100));
            marketInfoStat = MarketInfoStat_Min;
        }
        else if(sellPrice < 0.01f)
        {
            LogUtils.LogRealtime(String.format("%s is max price %.2f buyCount=%dh", code, buyPrice, (maxBuyInfo.amount+50)/100));
            marketInfoStat = MarketInfoStat_Max;
        }
        else
        {
            marketInfoStat = MarketInfoStat_Normal;
        }
    }

    public int GetMarketInfoStat()
    {
        return marketInfoStat;
    }

    public String toDesc()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Code:%s Name:%s Price: %.2f change;%.2f ratio:%.2f%%", this.code, name, priceNew, GetChange(), GetRatio()));
        return sb.toString();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Code:%s Name:%s\n", this.code, name));
        sb.append(String.format("Init:%.2f Last:%.2f\n", this.priceInit, this.priceLast));
        sb.append(String.format("Max :%.2f Min :%.2f\n", this.priceMax, this.priceMin));
        sb.append(String.format("Num :%d Rmb :%.2f万\n", this.tradeCount, this.tradeMoney/10000));
        sb.append(String.format("Trade Price: %.2f change;%.2f ratio:%.2f%%\n", this.priceNew, GetChange(), GetRatio()));
        int i;
        for(i=sellInfo.size()-1; i>= 0; --i)
        {
            if(sellInfo.get(i).price > 0.001)
            {
                sb.append(String.format("Sell%d: %.2f %d\n", i + 1, sellInfo.get(i).price, sellInfo.get(i).amount / 100));
            }
        }
        sb.append("----------------\n");
        for(i=0; i<buyInfo.size(); ++i)
        {
            if(buyInfo.get(i).price > 0.001)
            {
                sb.append(String.format("Buy %d: %.2f %d\n", i + 1, buyInfo.get(i).price, buyInfo.get(i).amount / 100));
            }
        }
        return  sb.toString();
    }
}
