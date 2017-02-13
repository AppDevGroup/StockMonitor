package com.wly.user;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.eastmoney.TradeEastmoneyImpl;
import com.wly.stock.policy.PolicyBase;
import com.wly.stock.policy.PolicyStep;
import com.wly.stock.policy.PolicyStepAll;
import io.netty.handler.codec.string.StringDecoder;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo
{
    public String id;
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
        tradeInterface = new TradeEastmoneyImpl();
        InitPolicySteps();
        Login(platAcct, platPsw);
    }

    private boolean InitPolicySteps()
    {
        try {
            PolicyStepAll policy;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from policy_step where user_id='%s'", id));
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
                policy.priceLast = rs.getFloat("pirce_last");
                policy.buyOrderId = rs.getString("buyorder_id");
                policy.sellOrderId = rs.getString("sellOrder_id");
                policy.lastDate = rs.getString("last_date");
                policySteps.add(policy);
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
        orderInfos.add(orderInfo);
        tradeInterface.DoOrder(orderInfo);
    }
    public void RevokeOrder(OrderInfo orderInfo){tradeInterface.RevokeOrder(orderInfo);};
    public void CheckOrderStatus(){tradeInterface.UpdateOrderStatus (orderInfos);}

    public OrderInfo DoTrade(String code, int tradeFlag, float price, int count)
    {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.id = Utils.GetId();
        orderInfo.code = code;
        orderInfo.tradeFlag = tradeFlag;
        orderInfo.count = count;
        orderInfo.orderPrice = price;

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
}
