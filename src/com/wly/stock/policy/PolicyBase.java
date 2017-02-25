package com.wly.stock.policy;

import com.wly.stock.common.StockPriceMonitor;
import com.wly.user.UserInfo;

/**
 * Created by Administrator on 2017/2/13.
 */
public abstract class PolicyBase extends StockPriceMonitor
{
    public int id;
    protected UserInfo userInfo;
    public PolicyBase(UserInfo uInfo)
    {
        userInfo = uInfo;
    }
}
