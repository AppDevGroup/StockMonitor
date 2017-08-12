package com.wly.stock.tradeplat.simulate;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.common.OrderInfo;

import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Administrator on 2017/2/14.
 */
public class SimulateEngine extends TimerTask
{
    private Timer timer;

    public void Start()
    {
        timer = new Timer();
        timer.schedule(this, 0, 1000);
    }

    @Override
    public void run()
    {
        try
        {
            ArrayList<OrderInfo> orderInfos = new ArrayList<>();
            OrderInfo orderInfo;
            String dateTime;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from trade_book where plat_id= 0 and stat in (%d, %d) " +
                            "and DATE_FORMAT(now(),'%%Y-%%m-%%d') = DATE_FORMAT(time,'%%Y-%%m-%%d')",
                    OrderInfo.OderStat_Order, OrderInfo.OderStat_Half));
            ResultSet rs = dbQuery.resultSet;
            while (rs.next())
            {
                orderInfo = new OrderInfo();
                orderInfo.id = rs.getInt("id");
                orderInfo.platId = rs.getInt("plat_id");
                orderInfo.platOrderId = rs.getString("plat_order_id");
                orderInfo.code = rs.getString("code");
                orderInfo.tradeFlag = rs.getInt("trade_flag");
                orderInfo.orderPrice = rs.getFloat("order_price");
                orderInfo.dealPrice = rs.getFloat("deal_price");
                orderInfo.count = rs.getInt("count");
                orderInfo.SetStat(rs.getInt("stat"));

                orderInfos.add(orderInfo);
            }
            dbQuery.Close();

            int i;
            for(i=0; i<orderInfos.size(); ++i)
            {
                Simulator(orderInfos.get(i));
            }
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_DB).error(ex.getMessage());
        }
    }

    private void Simulator(OrderInfo orderInfo)
    {
        boolean needUpdate = false;
        StockRuntimeInfo stockMarketInfo = StockMarketInfoManager.GetInstance().GetStockMarketInfoByCode(orderInfo.code);
        if(stockMarketInfo != null && stockMarketInfo.TestDeal(orderInfo.tradeFlag, orderInfo.orderPrice, orderInfo.count))
        {
            needUpdate =true;
            orderInfo.dealPrice = orderInfo.orderPrice;
            orderInfo.SetStat(OrderInfo.OderStat_Deal);
        }

        if(needUpdate)
        {
            try
            {
                final String UpdateFormat = "update trade_book SET stat = %d, deal_price=%.2f WHERE id = %d";
                DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.GetStat(), orderInfo.dealPrice,  orderInfo.id));
            }
            catch (Exception ex)
            {
                LogUtils.GetLogger(LogUtils.LOG_DB).error(ex.getMessage());
            }
        }
    }
}
