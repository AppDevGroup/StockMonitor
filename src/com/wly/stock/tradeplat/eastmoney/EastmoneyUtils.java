package com.wly.stock.tradeplat.eastmoney;

import com.wly.stock.common.OrderInfo;

/**
 * Created by wuly on 2017/8/12.
 */
public class EastmoneyUtils
{
    static public final String OrderStat_Order = "已报";
    static public final String OrderStat_Cancle = "已撤";
    static public final String OrderStat_WaitForCancel = "已报待撤";
    static public final String OrderStat_Half = "部成";
    static public final String OrderStat_Done = "成交";

    static public int GetStatByPlatStat(String str)
    {
        int stat = OrderInfo.OderStat_None;
        switch (str)
        {
            case OrderStat_Order:
                stat = OrderInfo.OderStat_Order;
                break;
            case OrderStat_Cancle:
                stat = OrderInfo.OderStat_Cancel;
                break;
            case OrderStat_Half:
                stat = OrderInfo.OderStat_Half;
                break;
            case OrderStat_WaitForCancel:
                stat = OrderInfo.OderStat_WaitForCancel;
                break;
            case OrderStat_Done:
                stat = OrderInfo.OderStat_Deal;
                break;
        }
        return  stat;
    }
}
