package com.wly.stock;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockMarketInfo
{
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
        this.buyInfo.clear();
        this.buyInfo.addAll(src.buyInfo);
        this.sellInfo.clear();
        this.sellInfo.addAll(src.sellInfo);
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
        sb.append(String.format("Num :%d Rmb :%.2f\n", this.tradeCount, this.tradeMoney));
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
