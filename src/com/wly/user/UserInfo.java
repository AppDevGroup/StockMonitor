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

    public UserInfo()
    {
        tradeInterface = new TradeEastmoneyImpl();
    }
}
