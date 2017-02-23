package com.wly.user;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockConst;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.StockPriceMonitorManager;
import com.wly.stock.common.*;
import com.wly.stock.policy.PolicyStep;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoneyImpl;
import com.wly.stock.tradeplat.simulate.TradeSimulateImpl;
import com.wly.stock.policy.PolicyBase;
import com.wly.stock.policy.PolicyStepAll;
import com.wly.stock.tradeplat.ITradeInterface;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo
{
    public int id;
    public String name;
    public int platId;
    public String platAcct;
    public String platPsw;
    public RmbAsset rmbAsset;
    public List<Asset> assets = new ArrayList<>();
    public List<PolicyBase> policySteps = new ArrayList<>();
    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();

    public ITradeInterface tradeInterface;

    public static void main(String[] args)
    {
    }

    public UserInfo()
    {
        rmbAsset = new RmbAsset();
        rmbAsset.code = StockConst.RmbCode;
        rmbAsset.code = StockConst.RmbName;
    }

    public void Init()
    {
        switch (platId)
        {
            case 1:
                tradeInterface = new TradeEastmoneyImpl();
                break;
            default:
                tradeInterface = new TradeSimulateImpl();
                break;
        }
        tradeInterface.SetUserInfo(this);
//        tradeInterface = new TradeEastmoneyImpl();
        InitPolicySteps();
        Login(platAcct, platPsw);
    }

    private boolean InitPolicySteps()
    {
        try {
            PolicyStepAll policy;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from policy_step where user_id=%d", id));
            ResultSet rs = dbQuery.resultSet;
            while (rs.next())
            {
                policy = new PolicyStepAll(this);
                policy.id = rs.getInt("id");
                policy.code = rs.getString("code");
                policy.priceInit = rs.getFloat("price_init");
                policy.initCount = rs.getInt("count_init");
                policy.priceUnit = rs.getFloat("price_unit");
                policy.stepUnit = rs.getInt("step_unit");
                policy.buyOffset = rs.getFloat("buy_offset");
                policy.sellOffset = rs.getFloat("sell_offset");
                policy.minPrice = rs.getFloat("min_price");
                policy.maxPrice = rs.getFloat("max_price");
                policy.policyStat = rs.getInt("policy_stat");
                policy.priceLast = rs.getFloat("price_last");
                policy.buyOrderId = rs.getString("buyorder_id");
                policy.buyLastPrice = rs.getFloat("buylast_price");
                policy.sellOrderId = rs.getString("sellOrder_id");
                policy.sellLastPrice = rs.getFloat("selllast_price");

                if(!policy.sellOrderId.equals("0") && !rs.getString("sellorder_date").equals(Utils.GetDate()))
                {
                    policy.sellOrderId = "0";
                }

                if(!policy.buyOrderId.equals("0") && !rs.getString("buyorder_date").equals(Utils.GetDate()))
                {
                    policy.sellOrderId = "0";
                }

                if(policy.policyStat == PolicyStepAll.PolicyStat_None)
                {
                    LogUtils.LogRealtime("policy is stop "+id);
                    continue;
                }

                policySteps.add(policy);

                StockMarketInfoManager.GetInstance().AddMonitorCode(policy.code);
                StockPriceMonitorManager.GetInstance().AddMonitor(policy);
            }
            dbQuery.Close();
            return true;
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            return  false;
        }
    }

    public void Login(String name, String psw)
    {
        tradeInterface.Login(name, psw);
    }
    public  void DoOrder(OrderInfo orderInfo)
    {
        try {
            orderInfos.add(orderInfo);
            LogUtils.LogTrade(String.format("DoTrade %d %s %d %.2f %d", id, orderInfo.code, orderInfo.tradeFlag, orderInfo.orderPrice, orderInfo.count));
            tradeInterface.DoOrder(orderInfo);
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
    }
    public void RevokeOrder(OrderInfo orderInfo)
    {
        if(orderInfo != null)
        {
            tradeInterface.RevokeOrder(orderInfo);
        }
        else
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).warn("try to revoke null order!");
        }
    }

    public OrderInfo DoTrade(String code, int tradeFlag, float price, int count)
    {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.code = code;
        orderInfo.tradeFlag = tradeFlag;
        orderInfo.count = count;
        orderInfo.orderPrice = price;
        orderInfo.dealPrice = 0;
        orderInfo.platId = platId;
        DoOrder(orderInfo);
        return orderInfo;
    }

    public void RevokeOrderByPlatId(String platOrderId)
    {
        RevokeOrder(GetOrderInfoByPlatId(platOrderId));
    }

    public int GetOrderStatByPlatId(String orderId)
    {
        int i;
        int ret = OrderInfo.OderStat_None;
        OrderInfo orderInfo = GetOrderInfoByPlatId(orderId);
        if (orderInfo != null)
        {
            ret = orderInfo.GetStat();
        }

        return ret;
    }

    public OrderInfo GetOrderInfoByPlatId(String orderId)
    {
        int i;
        OrderInfo ret = null;
        for(i=0; i<orderInfos.size(); ++i)
        {
            if(orderId == orderInfos.get(i).platOrderId)
            {
                ret = orderInfos.get(i);
                break;
            }
        }

        return ret;
    }

    public void UpdateOrderStat(OrderInfo orderInfo)
    {
        try {
            final String UpdateFormat = "update trade_book SET policy_stat = %d WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.id, orderInfo.GetStat()));
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
    }

    public void UpdateOrderPlatOrderId(OrderInfo orderInfo)
    {
        try
        {
            final String UpdateFormat = "update trade_book SET plat_order_id = %d WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.id, orderInfo.platOrderId));
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
    }
}
