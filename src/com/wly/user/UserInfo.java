package com.wly.user;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockConst;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.StockPriceMonitorManager;
import com.wly.stock.common.*;
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
        UserInfo uInfo = new UserInfo();
        uInfo.Login("514230600166072", "1251233321212");
       // uInfo.UpdateUserAsset();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.code = "601288";
        orderInfo.name = "农业银行";
        orderInfo.count = 100;
        orderInfo.orderPrice = 3.0f;
        orderInfo.tradeFlag = StockConst.TradeBuy;
        //uInfo.DoOrder(orderInfo);
//        uInfo.RevokeOrder(orderInfo);
        uInfo.CheckOrderStatus();
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
                policy.sellOrderId = rs.getString("sellOrder_id");

                if(!policy.sellOrderId.equals("0") && !rs.getString("sellorder_date").equals(Utils.GetDate()))
                {
                    policy.sellOrderId = "0";
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
            Utils.LogException(ex);
            return  false;
        }
    }

    public int AddOrder(OrderInfo orderInfo)
    {
        if(orderInfo.id == 0)
        {
            orderInfo.id = Utils.GetId();
        }
        orderInfos.add(orderInfo);
        return orderInfo.id;
    }

    public void RemoveOrder(int id)
    {
        if(id == 0)
        {
            return;
        }

        int i;
        for(i=0; i<orderInfos.size(); ++i)
        {
            if(orderInfos.get(i).id == id)
            {
                orderInfos.remove(i);
                break;
            }
        }
    }

    public void Login(String name, String psw)
    {
        tradeInterface.Login(name, psw);
    }
    public  void UpdateUserAsset(){tradeInterface.UpdateUserAsset();}
    public  void DoOrder(OrderInfo orderInfo)
    {
        try {
            final String UpdateFormat = "insert into trade_book(user_id, plat_id, code, trade_flag, " +
                    "order_price, 'deal_price', count, counter_fee, transfer_fee, stamp_tax, time) " +
                    "values(%d, '%s', '%s', %d, %.2f, %.2f, %d, %.2f, %.2f, %.2f, '%s')";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, id, platId, orderInfo.code,  orderInfo.tradeFlag,
                    orderInfo.orderPrice, orderInfo.dealPrice, orderInfo.count, 0, 0, 0, Utils.GetDate()));

            orderInfo.id = Utils.GetLastInserId();
            orderInfos.add(orderInfo);
            tradeInterface.DoOrder(orderInfo);
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
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
            System.out.println("try to revoke null order!");
        }
    }
    public void CheckOrderStatus(){tradeInterface.UpdateOrderStatus (orderInfos);}

    public OrderInfo DoTrade(String code, int tradeFlag, float price, int count)
    {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.code = code;
        orderInfo.tradeFlag = tradeFlag;
        orderInfo.count = count;
        orderInfo.orderPrice = price;
        orderInfo.orderPrice = 0;
        orderInfo.platId = platId;

        DoOrder(orderInfo);
        return orderInfo;
    }

    public Asset GetAsset(String code)
    {
        int i;
        Asset asset;
        for(i=0; i<assets.size(); ++i)
        {
            asset = assets.get(i);
            if (asset.code == code)
            {
                return asset;
            }
        }

        return null;
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
            Utils.LogException(ex);
        }
    }

    public void UpdateOrderPlatOrderId(OrderInfo orderInfo)
    {
        try
        {
            final String UpdateFormat = "update trade_book SET plat_order_id = %d WHERE id = %s";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.id, orderInfo.platOrderId));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }
}
