package com.wly.stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockInfo
{
    static public  class TradeInfo
    {
        public float price;
        public long amount;
    }

    public String code;
    public String name;
    public float priceInit;
    public float priceLastDay;
    public float priceNew;
    public float priceMax;
    public float priceMin;
    public float priceBuy;
    public float priceSell;
    public long tradeCount;
    public float tradeMoney;
    public ArrayList<TradeInfo> buyInfo = new ArrayList<TradeInfo>(5);
    public ArrayList<TradeInfo> sellInfo = new ArrayList<TradeInfo>(5);

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Code:%s Name:%s\n", this.code, name));
        sb.append(String.format("Init:%.2f Last:%.2f\n", this.priceInit, this.priceLastDay));
        sb.append(String.format("Max :%.2f Min :%.2f\n", this.priceMax, this.priceMin));
        sb.append(String.format("Num :%d Rmb :%.2f\n", this.tradeCount, this.tradeMoney));
        sb.append(String.format("Trade Price: %.2f\n", this.priceNew));
        int i;
        for(i=sellInfo.size()-1; i>= 0; --i)
        {
            sb.append(String.format("Sell%d: %.2f %d\n", i+1, sellInfo.get(i).price, sellInfo.get(i).amount/100));
        }
        sb.append("----------------\n");
        for(i=0; i<buyInfo.size(); ++i)
        {
            sb.append(String.format("Buy %d: %.2f %d\n", i+1, buyInfo.get(i).price, buyInfo.get(i).amount/100));
        }
        return  sb.toString();
    }
}
