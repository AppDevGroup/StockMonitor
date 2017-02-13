package com.wly.stock.policy;

import com.wly.stock.StockMarketInfo;
import com.wly.user.UserInfo;

/**
 * Created by Administrator on 2017/2/13.
 */
public abstract class PolicyBase
{
    public int id;
    public String code;
    protected UserInfo userInfo;
    public PolicyBase(UserInfo uInfo)
    {
        userInfo = uInfo;
    }

    public abstract  void PrcessPrice(StockMarketInfo stockMarketInfo);
}
