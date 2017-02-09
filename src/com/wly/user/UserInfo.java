package com.wly.user;

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
        uInfo.Login("5434060016607212", "1252312332");
    }

    public UserInfo()
    {
        tradeInterface = new TradeEastmoneyImpl();
    }

    public void Login(String name, String psw)
    {
        tradeInterface.Login(name, psw);
    }
}
