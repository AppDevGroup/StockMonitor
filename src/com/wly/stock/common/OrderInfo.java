package com.wly.stock.common;

/**
 * Created by Administrator on 2017/2/9.
 */
public class OrderInfo
{
    static public final int OderStat_None = 0; //未知状态
    static public final int OderStat_Ready = 1; //预备状态
    static public final int OderStat_Order = 2; //下单
    static public final int OderStat_Deal = 3;  //已成交

    public int id;
    public String code;
    public String name;
    public  int tradeFlag;
    public int count;
    public float orderPrice;    //订单价格
    public float dealPrice;     //成交价格
    public String platId;       //交易平台id
    public int orderStat = OderStat_None;   //订单状态

    @Override
    public String toString()
    {
        final String strFormat = "id=%s code=%s name=%s tradeFlag=%s count=%d orderPrice=%.2f dealPrice=%.2f orderStat=%d\n";
        return String.format(strFormat, id, code, name, tradeFlag, count, orderPrice, dealPrice, orderStat);
    }
}
