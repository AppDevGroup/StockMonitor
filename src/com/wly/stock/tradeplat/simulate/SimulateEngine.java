package com.wly.stock.tradeplat.simulate;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.policy.PolicyStepAll;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from trade_book where plat_id= 0"));
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

            int i;
            for(i=0; i<orderInfos.size(); ++i)
            {
                Simulator(orderInfos.get(i));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void Simulator(OrderInfo orderInfo)
    {
        boolean needUpdate = false;
        int val;
        switch (orderInfo.GetStat())
        {
            case OrderInfo.OderStat_Order:
                val = GetRamdomDeal();
                if(val == 0)
                {
                    needUpdate =true;
                    orderInfo.SetStat(OrderInfo.OderStat_Deal);
                }
                else if(val == 1)
                {
                    needUpdate =true;
                    orderInfo.SetStat(OrderInfo.OderStat_Half);
                }
                break;
            case OrderInfo.OderStat_Half:
                val = GetRamdomDeal();
                if(val == 0)
                {
                    needUpdate =true;
                    orderInfo.SetStat(OrderInfo.OderStat_Deal);
                }
                break;
            case OrderInfo.OderStat_WaitForCancel:
                needUpdate =true;
                orderInfo.SetStat(OrderInfo.OderStat_Cancel);
                break;
        }

        if(needUpdate)
        {
            try
            {
                final String UpdateFormat = "update trade_book SET stat = %d WHERE id = %d";
                DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.GetStat(),  orderInfo.id));
            }
            catch (Exception ex)
            {
                Utils.LogException(ex);
            }
        }
    }

    //0-成交 1-部分成交 2-未成交
    private int GetRamdomDeal()
    {
        int ret = 0;
        int val = new Random().nextInt(10000);
        if(val< 3000)
        {
            ret = 0;
        }
        else if(val < 7000)
        {
            ret = 1;
        }
        else
        {
            ret = 2;
        }
        return  ret;
    }
}
