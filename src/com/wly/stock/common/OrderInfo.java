package com.wly.stock.common;

/**
 * Created by Administrator on 2017/2/9.
 */
public class OrderInfo
{
    static public final int OderStat_None = 0; //未知状态
    static public final int OderStat_Ready = 1; //预备状态
    static public final int OderStat_Order = 2; //已下单
    static public final int OderStat_Deal = 3;  //已成交
    static public final int OderStat_Half = 4; //部分成交
    static public final int OderStat_Cancel = 5; //已撤销
    static public final int OderStat_WaitForCancel = 6; //已报待撤销

    public int id;
    public String code;
    public String name;
    public  int tradeFlag;
    public int count;
    public float orderPrice;    //订单价格
    public float dealPrice;     //成交价格
    public String platOrderId;       //交易平台订单id

    private int orderStat = OderStat_None;   //订单状态
    public boolean isNewStat = false;

    public void SetStat(int newStat)
    {
        if (orderStat != newStat)
        {
            isNewStat = true;
            orderStat = newStat;
        }
    }

    public int GetStat()
    {
        return orderStat;
    }

    @Override
    public String toString()
    {
        final String strFormat = "id=%s code=%s name=%s tradeFlag=%s count=%d orderPrice=%.2f dealPrice=%.2f orderStat=%d\n";
        return String.format(strFormat, id, code, name, tradeFlag, count, orderPrice, dealPrice, orderStat);
    }
}
