package com.wly.stock.policy;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.stock.StockConst;
import com.wly.stock.StockMarketInfo;
import com.wly.stock.StockUtils;
import com.wly.stock.common.OrderInfo;
import com.wly.user.Asset;
import com.wly.user.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/13.
 */
public class PolicyStepAll extends PolicyBase
{
    public static final  int PolicyStat_None = 0; //未启用
    public static final  int PolicyStat_Init = 1; //待初始化
    public static final  int PolicyStat_Step = 2; //区间执行
    public static final  int PolicyStat_Finish = 3; //全部卖完

    public int userId;
    public  float priceInit;    //初始买入价格
    public int initCount;       //初始买入数量
    public  float priceUnit;    //价格区间
    public  int stepUnit;       //每笔数量
    public float buyOffset;     //买入价格修正
    public float sellOffset;    //卖出价格修正
    public float minPrice;      //策略最小价格
    public float maxPrice;      //策略最大价格

    public int policyStat;    //策略状态
    public  float priceLast;    //最新交易价格

    public String buyOrderId;
    public String sellOrderId;

    private float buyLastPrice;     //买入成交更新参考价格
    private float sellLastPrice;    //卖出成交更新参考价格

    public PolicyStepAll(UserInfo uInfo)
    {
        super(uInfo);
    }

    public  void OnNewPirce(StockMarketInfo stockMarketInfo)
    {
        System.out.println("PolicyStepAll:\\n"+stockMarketInfo.toDesc());
        if(stockMarketInfo.priceNew < 0.1f)
        {
            Utils.Log("error price for :"+ stockMarketInfo.code+" price: "+ stockMarketInfo.priceNew);
            return;
        }

        if(!stockMarketInfo.code.equals(code))
        {
            Utils.Log(String.format("code mismatch! policyCode=%s stockMarketInfo code=%s", code, stockMarketInfo.code));
            return;
        }

        if(policyStat == PolicyStat_None)
        {
            return;
        }

        float change = stockMarketInfo.priceNew- stockMarketInfo.priceLast;
        float changeRatio = change/ stockMarketInfo.priceLast;

        if(Math.abs(changeRatio) > 0.11)
        {
            Utils.Log("exception price for :"+ stockMarketInfo.code+" price: "+ stockMarketInfo.priceNew);
            return;
        }

       switch (policyStat)
       {
           case PolicyStat_Step:
               ProcessStep(stockMarketInfo);
               break;
           case PolicyStat_Init:
               ProcessInit(stockMarketInfo);
               break;
       }
    }

    public  void ProcessStep(StockMarketInfo stockMarketInfo)
    {
        float change = stockMarketInfo.priceNew- stockMarketInfo.priceLast;
        float changeRatio = change/ stockMarketInfo.priceLast;

        float priceBuy = priceLast-priceUnit+buyOffset;
        float priceSell = priceLast+priceUnit+sellOffset;

        System.out.println(String.format("PrcessPrice code:%s  priceLast:%.2f priceBuy:%.2f priceSell:%.2f priceNew:%.2f change:%+.2f changeRatio:%+.2f",
                code, priceLast, priceBuy, priceSell, stockMarketInfo.priceNew, change, changeRatio*100 ));

        float offset;
        int unitCount, i, maxUnitCount;
        int tradeCount;
        float tradePrice;
        float tradeFee;
        OrderInfo orderInfo;

        StockMarketInfo.TradeInfo sellTradeInfo;
        StockMarketInfo.TradeInfo buyTradeInfo;

        sellTradeInfo = stockMarketInfo.sellInfo.get(0);
        if(sellTradeInfo.price < minPrice)
        {
            return;
        }

        Asset asset = userInfo.GetAsset(code);
        asset = new Asset();
        asset.code = code;
        asset.activeCount = 999999999;
        buyTradeInfo = stockMarketInfo.buyInfo.get(0);
        if(stockMarketInfo.TestDeal(StockConst.TradeSell, maxPrice, asset.activeCount))
        {
            OrderInfo sellOrder = userInfo.DoTrade(code, StockConst.TradeSell, maxPrice, asset.activeCount);
            policyStat = PolicyStat_Finish;
            return;
        }

//        if(sellOrderId == null || sellOrderId.equals("0"))
        if(!HasSellOrder())
        {
            if (buyTradeInfo.price >= priceSell)
            {
                //doSell
                offset = buyTradeInfo.price - priceLast;
                unitCount = (int) ((offset - sellOffset) / priceUnit);
                tradeCount = stepUnit * unitCount;
                tradeCount = tradeCount >= asset.activeCount ? asset.activeCount : tradeCount;
                tradePrice = priceLast + unitCount * priceUnit + sellOffset;

                if (stockMarketInfo.TestDeal(StockConst.TradeSell, tradePrice, tradeCount))
                {
                    orderInfo = userInfo.DoTrade(code, StockConst.TradeSell, tradePrice, tradeCount);
                    sellLastPrice = priceLast + priceUnit * unitCount;
                    sellOrderId = orderInfo.platOrderId;
                    StoreSellOrder(sellOrderId);
                }
                else if (unitCount > 1)
                {
                    unitCount = unitCount - 1;
                    tradePrice = priceLast + unitCount * priceUnit + sellOffset;
                    orderInfo = userInfo.DoTrade(code, StockConst.TradeSell, tradePrice, tradeCount);
                    sellLastPrice = priceLast + priceUnit * unitCount;
                    sellOrderId = orderInfo.platOrderId;
                    StoreSellOrder(sellOrderId);
                }
            }
        }
        else
        {
            CheckSellOrder();
        }

//        if(buyOrderId == null || buyOrderId.equals("0"))
        if(!HasBuyOrder())
        {
            if(sellTradeInfo.price <= priceBuy)
            {
                //doBuy
                offset = priceLast- sellTradeInfo.price;
                unitCount = (int)((offset+buyOffset)/priceUnit);
                tradeCount = stepUnit*unitCount;
                tradePrice = priceLast-unitCount*priceUnit+buyOffset;
                if(stockMarketInfo.TestDeal(StockConst.TradeBuy, tradePrice, tradeCount))
                {
                    tradeFee = userInfo.tradeInterface.CacuTradeFee(StockConst.TradeBuy, code, tradePrice, tradeCount);
                    if(userInfo.rmbAsset.activeAmount >= tradePrice*tradeCount+tradeFee)
                    {
                        orderInfo = userInfo.DoTrade(code, StockConst.TradeBuy, tradePrice, tradeCount);
                        buyLastPrice = priceLast - priceUnit * unitCount;
                        buyOrderId = orderInfo.platOrderId;
                        StoreBuyOrderId(buyOrderId);
                    }
                    else
                    {
                        System.out.println(String.format("Money not enough for buy code=%s price=%.2f count=%d need=%.2f current=%.2f",
                                code, tradePrice, tradeCount, tradePrice*tradeCount+tradeFee, userInfo.rmbAsset.activeAmount));
                    }
                }
            }
        }
        else
        {
            CheckBuyOrder();
        }
    }

    private void ProcessInit(StockMarketInfo stockMarketInfo)
    {
        if(HasBuyOrder())
        {
            return;
        }

        if(stockMarketInfo.TestDeal(StockConst.TradeBuy, priceInit, initCount))
        {
            OrderInfo orderInfo = userInfo.DoTrade(code, StockConst.TradeBuy, priceInit, initCount);
            buyOrderId = orderInfo.platOrderId;
            StoreBuyOrderId(buyOrderId);
        }
    }

    private void CheckBuyOrder()
    {
        if(userInfo.GetOrderStatByPlatId(buyOrderId) == OrderInfo.OderStat_Deal)
        {
            if(policyStat == PolicyStat_Init)
            {
                policyStat = PolicyStat_Step;
                priceLast = priceInit;
                StorePolicyStat();
            }
            else
            {
                priceLast = buyLastPrice;
                buyLastPrice = 0f;
            }
            StoreLastPrice();
            buyOrderId = "0";
            StoreBuyOrderId(buyOrderId);

            if(sellOrderId != null || !sellOrderId.equals("0"))
            {
                userInfo.RevokeOrderByPlatId(sellOrderId);
                sellOrderId = "0";
                StoreSellOrder(sellOrderId);
            }
        }
    }

    private void CheckSellOrder()
    {
        if(userInfo.GetOrderStatByPlatId(sellOrderId) == OrderInfo.OderStat_Deal)
        {
            if(policyStat == PolicyStat_Finish)
            {
                policyStat = PolicyStat_Init;
                priceLast = 0f;
                StorePolicyStat();
            }

            priceLast = sellLastPrice;
            sellLastPrice = 0f;
            StoreLastPrice();
            sellOrderId = "0";
            StoreSellOrder(sellOrderId);

            if(buyOrderId != null && !buyOrderId.equals("0"))
            {
                userInfo.RevokeOrderByPlatId(buyOrderId);
                buyOrderId = "0";
                StoreBuyOrderId(buyOrderId);
            }
        }
    }

    private void StoreBuyOrderId(String buyId)
    {
        try {
            final String UpdateFormat = "update policy_step SET sellorder_id = '%s', sellorder_date='%s' WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, buyId, Utils.GetDate(), id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    private void StoreSellOrder(String sellId)
    {
        try {
            final String UpdateFormat = "update policy_step SET buyorder_id = %s, buyorder_date='%s' WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, sellId, Utils.GetDate(),id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    private void StorePolicyStat()
    {
        try {
            final String UpdateFormat = "update policy_step SET policy_stat = %d WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, policyStat, id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    private  void StoreLastPrice()
    {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间
            final String UpdateFormat = "update policy_step SET price_last = %.2f last_date=%s  WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, priceLast, date, id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    private boolean HasBuyOrder()
    {
        return buyOrderId != null && !buyOrderId.equals("0");
    }

    private boolean HasSellOrder()
    {
        return sellOrderId != null && !sellOrderId.equals("0");
    }
}
