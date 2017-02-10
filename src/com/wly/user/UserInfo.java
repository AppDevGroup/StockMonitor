package com.wly.user;

import com.wly.stock.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.eastmoney.TradeEastmoneyImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo
{
    public String id;
    public String name;
    public List<Asset> assets = new ArrayList<>();
    public List<OrderInfo> orderList = new ArrayList<>();

    public ITradeInterface tradeInterface;

    public static void main(String[] args)
    {
        UserInfo uInfo = new UserInfo();
        uInfo.Login("53423406001660721234", "12235aw3s3212");
        uInfo.FillUserAsset();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.code = "601288";
        orderInfo.name = "农业银行";
        orderInfo.count = 100;
        orderInfo.orderPrice = 3.00f;
        orderInfo.tradeFlag = StockConst.TradeBuy;
        uInfo.DoOrder(orderInfo);
    }

    public UserInfo()
    {
        tradeInterface = new TradeEastmoneyImpl();
    }

    public void Login(String name, String psw)
    {
        tradeInterface.Login(name, psw);
    }
    public  void FillUserAsset(){tradeInterface.FillUserAsset(this);}
    public  void DoOrder(OrderInfo orderInfo){ tradeInterface.DoOrder(orderInfo);}
}
