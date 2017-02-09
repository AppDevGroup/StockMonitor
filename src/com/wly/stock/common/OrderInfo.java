package com.wly.stock.common;

/**
 * Created by Administrator on 2017/2/9.
 */
public class OrderInfo
{
    public final int OderStat_None = 0;
    public final int OderStat_Order = 1; //下单
    public final int OderStat_Deal = 2;  //已成交

    public int id;
    public String code;
    public  int tradeFlag;
    public int count;
    public float orderPrice;    //订单价格
    public float dealPrice;     //成交价格
    public int orderStat;   //订单状态

    @Override
    public String toString()
    {
        final String strFormat = "id=%s code=%s tradeFlag=%s count=%d orderPrice=%.2f dealPrice=%.2f orderStat=%d\n";
        return String.format(strFormat, id, code, tradeFlag, count, orderPrice, dealPrice, orderStat);
    }
}
