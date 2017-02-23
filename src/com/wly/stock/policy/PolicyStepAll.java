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
    public static final  int PolicyStat_Exception = 4; //异常 停止策略

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

    public float buyLastPrice;     //买入成交更新参考价格
    public float sellLastPrice;    //卖出成交更新参考价格

    public PolicyStepAll(UserInfo uInfo)
    {
        super(uInfo);
    }

    public  void OnNewPirce(StockMarketInfo stockMarketInfo)
    {
        if(policyStat == PolicyStat_None)
        {
//            System.out.println("policy is stop "+id);
            return;
        }

        if(policyStat == PolicyStat_Exception)
        {
            System.out.println("policy is Exception "+id);
            return;
        }

        if(!stockMarketInfo.code.equals(code))
        {
            System.out.println(String.format("code mismatch! policyCode=%s stockMarketInfo code=%s", code, stockMarketInfo.code));
            return;
        }

        if(stockMarketInfo.priceNew < 0.1f)
        {
            System.out.println("error price for :"+ stockMarketInfo.code+" price: "+ stockMarketInfo.priceNew);
            return;
        }

        if(stockMarketInfo.GetMarketInfoStat() == StockMarketInfo.MarketInfoStat_Bidding)
        {
            System.out.println("time of bidding! "+stockMarketInfo.toDesc());
            return;
        }
       else if(stockMarketInfo.GetMarketInfoStat() == StockMarketInfo.MarketInfoStat_None)
        {
            System.out.println("unkonw marketinfo none stat!"+stockMarketInfo.toDesc());
            return;
        }

        float change = stockMarketInfo.priceNew- stockMarketInfo.priceLast;
        float changeRatio = change/ stockMarketInfo.priceLast;

        if(Math.abs(changeRatio) > 0.11)
        {
            Utils.Log("exception price for :"+ stockMarketInfo.code+" price: "+ stockMarketInfo.priceNew);
            policyStat = PolicyStat_Exception;
            StorePolicyStat();
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

        System.out.println(String.format("PrcessPrice step pilicy:%d code:%s  priceLast:%.2f priceBuy:%.2f priceSell:%.2f priceNew:%.2f change:%+.2f changeRatio:%+.2f",
                id, code, priceLast, priceBuy, priceSell, stockMarketInfo.priceNew, change, changeRatio*100 ));

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
            System.out.println(String.format("policy:%d code=%s stock sell price:%.2f lower than min:%.2f ", id, code, sellTradeInfo.price , minPrice));
            return;
        }

        int stockCount = userInfo.tradeInterface.GetStockAssetCount(code);

        buyTradeInfo = stockMarketInfo.buyInfo.get(0);
        if(stockMarketInfo.TestDeal(StockConst.TradeSell, maxPrice, stockCount))
        {
            OrderInfo sellOrder = userInfo.DoTrade(code, StockConst.TradeSell, maxPrice, stockCount);
            policyStat = PolicyStat_Finish;
            return;
        }

//        if(sellOrderId == null || sellOrderId.equals("0"))
        if(stockCount > 0)
        {
            if (!HasSellOrder())
            {

                unitCount = 0;
                if (buyTradeInfo.price >= priceSell)
                {
                    //doSell
                    offset = buyTradeInfo.price - priceLast;
                    unitCount = (int) ((offset - sellOffset) / priceUnit);
                }
                unitCount = unitCount < 1 ? 1 : unitCount;

                tradeCount = stepUnit * unitCount;
                tradeCount = tradeCount >= stockCount ? stockCount : tradeCount;
                tradePrice = priceLast + unitCount * priceUnit + sellOffset;

                if (unitCount == 1 || stockMarketInfo.TestDeal(StockConst.TradeSell, tradePrice, tradeCount))
                {
                    orderInfo = userInfo.DoTrade(code, StockConst.TradeSell, tradePrice, tradeCount);
                    sellLastPrice = priceLast + priceUnit * unitCount;
                    sellOrderId = orderInfo.platOrderId;
                    StoreSellOrder(sellOrderId);
                } else if (unitCount > 1)
                {
                    unitCount = unitCount - 1;
                    tradePrice = priceLast + unitCount * priceUnit + sellOffset;
                    orderInfo = userInfo.DoTrade(code, StockConst.TradeSell, tradePrice, tradeCount);
                    sellLastPrice = priceLast + priceUnit * unitCount;
                    sellOrderId = orderInfo.platOrderId;
                    StoreSellOrder(sellOrderId);
                }
            } else
            {
                CheckSellOrder();
            }
        }
        else
        {
            System.out.println(String.format("policy:%d code=%s no stock can be sell", id, code));
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
                    if(userInfo.tradeInterface.GetRmbAsset() >= tradePrice*tradeCount+tradeFee)
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
            CheckBuyOrder();
            return;
        }

        System.out.println(String.format("PrcessPrice init policy:%d code:%s  priceBuy:%.2f priceSellNow:%.2f",
                id, code, priceInit, stockMarketInfo.sellInfo.get(0).price));

        if(stockMarketInfo.TestDeal(StockConst.TradeBuy, priceInit, initCount))
        {
            OrderInfo orderInfo = userInfo.DoTrade(code, StockConst.TradeBuy, priceInit, initCount);
            buyOrderId = orderInfo.platOrderId;
            StoreBuyOrderId(buyOrderId);
        }
    }

    private void CheckBuyOrder()
    {
        int stat = userInfo.tradeInterface.GetOrderStatus(buyOrderId);
        System.out.println( String.format("CheckBuyOrder policy:%d code=%s price:%.2f stat:%s ", id, code, buyLastPrice, OrderInfo.GetSOrderInfoStatDesc(stat)));

        if(stat == OrderInfo.OderStat_Deal)
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
        int stat = userInfo.tradeInterface.GetOrderStatus(sellOrderId);
        System.out.println( String.format("CheckSellOrder policy:%d code=%s price=%.2f stat:%s ", id, code, sellLastPrice, OrderInfo.GetSOrderInfoStatDesc(stat)));
        if(stat == OrderInfo.OderStat_Deal)
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
            final String UpdateFormat = "update policy_step SET buyorder_id = '%s', buylast_price, buyorder_date='%s' WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, buyId, buyLastPrice, Utils.GetDate(), id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    private void StoreSellOrder(String sellId)
    {
        try {
            final String UpdateFormat = "update policy_step SET sellorder_id = '%s', selllast_price=%.2f,sellorder_date='%s' WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, sellId, sellLastPrice, Utils.GetDate(),id));
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
            final String UpdateFormat = "update policy_step SET price_last = %.2f WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, priceLast, id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    private boolean HasBuyOrder()
    {
        return buyOrderId != null && !buyOrderId.equals("0") && !buyOrderId.equals("null");
    }

    private boolean HasSellOrder()
    {
        return sellOrderId != null && !sellOrderId.equals("0") && !sellOrderId.equals("null");
    }
}
