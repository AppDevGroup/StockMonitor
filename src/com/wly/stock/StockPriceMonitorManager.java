package com.wly.stock;

import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.common.StockPriceMonitor;

import java.util.*;

/**
 * Created by Administrator on 2017/2/13.
 */
public class StockPriceMonitorManager extends TimerTask
{
    private static StockPriceMonitorManager sInstance = null;
    public static StockPriceMonitorManager GetInstance()
    {
        if(sInstance == null)
        {
            sInstance = new StockPriceMonitorManager();
        }

        return sInstance;
    }

    private HashMap<String, ArrayList<StockPriceMonitor>> monitorListHashMap = new HashMap<>();
    private Timer timer;

    public void AddMonitor(StockPriceMonitor monitor)
    {
        if(monitorListHashMap.containsKey(monitor.code))
        {
            monitorListHashMap.get(monitor.code).add(monitor);
        }
        else
        {
            ArrayList<StockPriceMonitor> stockPriceMonitors = new ArrayList<>();
            stockPriceMonitors.add(monitor);
            monitorListHashMap.put(monitor.code, stockPriceMonitors);
        }
    }


    public void Start()
    {
        timer = new Timer();
        timer.schedule(this, 0, 1000);
    }

    @Override
    public void run()
    {
        if(!StockMarketInfoManager.GetInstance().GetHasInited())
        {
            return;
        }

        Iterator iter = monitorListHashMap.entrySet().iterator();
        int i;
        Map.Entry entry;
        ArrayList<StockPriceMonitor> monotorList;
        StockRuntimeInfo stockMarketInfo;
        while (iter.hasNext())
        {
            entry = (Map.Entry) iter.next();
            monotorList = (ArrayList<StockPriceMonitor>)entry.getValue();
            stockMarketInfo = StockMarketInfoManager.GetInstance().GetStockMarketInfoByCode((String)entry.getKey());
            if(stockMarketInfo == null)
            {
                System.out.println("StockPriceMonitorManager get stockInfo Failed! "+(String)entry.getKey());
                continue;
            }
            for(i=0; i<monotorList.size(); ++i)
            {
                monotorList.get(i).OnNewPirce(stockMarketInfo);
            }
        }
    }
}
