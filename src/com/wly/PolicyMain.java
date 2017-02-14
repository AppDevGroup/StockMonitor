package com.wly;

import com.wly.database.DBPool;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.StockPriceMonitorManager;
import com.wly.stock.tradeplat.simulate.SimulateEngine;
import com.wly.user.UserInfo;

/**
 * Created by Administrator on 2017/2/13.
 */
public class PolicyMain
{
    static public  void main(String args)
    {
        DBPool dbPool = DBPool.GetInstance();
        dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitor", "root", "123456");

        StockMarketInfoManager stockMarketInfoManager = StockMarketInfoManager.GetInstance();
        stockMarketInfoManager.Start();

        StockPriceMonitorManager stockPriceMonitorManager = StockPriceMonitorManager.GetInstance();
        stockPriceMonitorManager.Start();

        SimulateEngine simulateEngine =new SimulateEngine();
        simulateEngine.Start();

        UserInfoManager userInfoManager = UserInfoManager.GetInstance();
        userInfoManager.Init();
    }
}
