package com.wly;

import com.wly.database.DBPool;
import com.wly.stock.StockInfoProviderSimulator;
import com.wly.stock.StockInfoProviderSina;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.StockPriceMonitorManager;
import com.wly.stock.tradeplat.simulate.SimulateEngine;
import com.wly.user.UserInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/13.
 */
public class PolicyMain
{
    static public  void main(String[] args)
    {
        DBPool dbPool = DBPool.GetInstance();
        dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitor?useSSL=true", "root", "123456");

        StockMarketInfoManager stockMarketInfoManager = StockMarketInfoManager.GetInstance();

        if(args.length > 0 && args[0].equals("0"))
        {
            stockMarketInfoManager.SetStockInfoProvider(new StockInfoProviderSimulator());
        }
        else
        {
             stockMarketInfoManager.SetStockInfoProvider(new StockInfoProviderSina());
        }

        stockMarketInfoManager.Start();

        StockPriceMonitorManager stockPriceMonitorManager = StockPriceMonitorManager.GetInstance();
        stockPriceMonitorManager.Start();

        SimulateEngine simulateEngine = new SimulateEngine();
        simulateEngine.Start();

        ArrayList<Integer> platList = new ArrayList<>();
        if(args.length > 0 && args[0].equals("0"))
        {
            platList.add(0);
        }
        UserInfoManager userInfoManager = UserInfoManager.GetInstance();
        userInfoManager.Init(platList);
    }
}
