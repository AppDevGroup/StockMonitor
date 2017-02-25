package com.wly.stock.common;

/**
 * Created by Administrator on 2017/2/9.
 */
public class TradeBook
{
    public String id;
    public String code;
    public int sellFlag;    //0-买 1-卖
    public float tradePrice;
    public String tradeTime;

    @Override
    public String toString()
    {
        final String strFormat = "id=%s code=%s sellFlag=%s tradePrice=%.2f tradeTime=%s\n";
        return String.format(strFormat, id, code, sellFlag== StockConst.TradeSell?"sell":"buy", tradePrice, tradeTime);
    }
}
