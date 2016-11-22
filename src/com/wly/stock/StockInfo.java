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
        public int price;
        public int amount;
    }

    public int code;
    public String name;
    public int priceInit;
    public int priceLastDay;
    public int priceNow;
    public int priceMax;
    public int priceMin;
    public int priceBuy;
    public int priceSell;
    public int tradeCount;
    public int tradeMoney;
    public ArrayList<TradeInfo> buyInfo = new ArrayList<TradeInfo>(5);
    public ArrayList<TradeInfo> sellInfo = new ArrayList<TradeInfo>(5);
}
