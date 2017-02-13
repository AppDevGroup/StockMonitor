package com.wly.user;

import com.wly.common.Utils;
import com.wly.stock.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.eastmoney.TradeEastmoneyImpl;
import com.wly.stock.policy.PolicyStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo
{
    public String id;
    public String name;
    public Asset rmbAsset;
    public List<Asset> assets = new ArrayList<>();
    public List<PolicyStep> policySteps = new ArrayList<>();
    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();

    public ITradeInterface tradeInterface;

    public static void main(String[] args)
    {
        UserInfo uInfo = new UserInfo();
        uInfo.Login("514230600166072", "1251233321212");
       // uInfo.FillUserAsset();

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
        rmbAsset = new Asset();
        rmbAsset.code = StockConst.RmbCode;
        rmbAsset.code = StockConst.RmbName;
        tradeInterface = new TradeEastmoneyImpl();
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
    public  void FillUserAsset(){tradeInterface.FillUserAsset(this);}
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
