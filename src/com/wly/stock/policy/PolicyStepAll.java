package com.wly.stock.policy;

import com.wly.common.Utils;
import com.wly.stock.StockConst;
import com.wly.stock.StockMarketInfo;
import com.wly.stock.StockUtils;
import com.wly.stock.common.OrderInfo;
import com.wly.user.Asset;
import com.wly.user.UserInfo;

/**
 * Created by Administrator on 2017/2/13.
 */
public class PolicyStepAll extends PolicyBase
{
    private final  int PolicyStat_None = 0; //未启用
    private final  int PolicyStat_Init = 1; //待初始化
    private final  int PolicyStat_Step = 2; //区间执行
    private final  int PolicyStat_Finish = 3; //全部卖完

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

    private OrderInfo sellOrder;    //买入订单
    private float buyLastPrice;     //买入成交更新参考价格
    private OrderInfo buyOrder;     //卖出订单
    private float sellLastPrice;    //卖出成交更新参考价格

    public PolicyStepAll(UserInfo uInfo)
    {
        super(uInfo);
    }

    public  void PrcessPrice(StockMarketInfo stockMarketInfo)
    {
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

        StockMarketInfo.TradeInfo sellTradeInfo;
        StockMarketInfo.TradeInfo buyTradeInfo;

        sellTradeInfo = stockMarketInfo.sellInfo.get(0);
        if(sellTradeInfo.price < minPrice)
        {
            return;
        }

        Asset asset = userInfo.GetAsset(code);
        buyTradeInfo = stockMarketInfo.buyInfo.get(0);
        if(stockMarketInfo.TestDeal(StockConst.TradeSell, maxPrice, asset.activeCount))
        {

            sellOrder = userInfo.DoTrade(code, StockConst.TradeSell, maxPrice, asset.activeCount);
            policyStat = PolicyStat_Finish;
            return;
        }

        if(sellOrder == null)
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
                    sellOrder = userInfo.DoTrade(code, StockConst.TradeSell, tradePrice, tradeCount);
                    sellLastPrice = priceLast + priceUnit * unitCount;
                } else if (unitCount > 1)
                {
                    unitCount = unitCount - 1;
                    tradePrice = priceLast + unitCount * priceUnit + sellOffset;
                    sellOrder = userInfo.DoTrade(code, StockConst.TradeSell, tradePrice, tradeCount);
                    priceLast = priceLast + priceUnit * unitCount;
                    sellLastPrice = priceLast + priceUnit * unitCount;
                }
            }
        }
        else
        {
            CheckSellOrder();
        }

        if(buyOrder == null)
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
                    if(userInfo.rmbAsset.activeCount >= tradePrice*tradeCount+tradeFee)
                    {
                        buyOrder = userInfo.DoTrade(code, StockConst.TradeBuy, tradePrice, tradeCount);
                        buyLastPrice = priceLast - priceUnit * unitCount;
                    }
                    else
                    {
                        System.out.println(String.format("Money not enough for buy code=%s price=%.2f count=%d need=%.2f current=%.2f",
                                code, tradePrice, tradeCount, tradePrice*tradeCount+tradeFee, userInfo.rmbAsset.activeCount));
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
        if(stockMarketInfo.TestDeal(StockConst.TradeBuy, priceInit, initCount))
        {
            userInfo.DoTrade(code, StockConst.TradeBuy, priceInit, initCount);
            policyStat = PolicyStat_Step;
        }
    }

    private void CheckBuyOrder()
    {
        if(buyOrder.isNewStat && buyOrder.GetStat() == OrderInfo.OderStat_Deal)
        {
            if(policyStat == PolicyStat_Init)
            {
                policyStat = PolicyStat_Step;
                priceLast = priceInit;
            }
            else
            {
                priceLast = buyLastPrice;
                buyLastPrice = 0f;
            }

            UpdateLastPrice();

            if(sellOrder != null)
            {
                userInfo.RevokeOrder(sellOrder);
                sellOrder = null;
            }
        }
    }

    private void CheckSellOrder()
    {
        if(sellOrder.isNewStat && sellOrder.GetStat() == OrderInfo.OderStat_Deal)
        {
            if(policyStat == PolicyStat_Finish)
            {
                policyStat = PolicyStat_Init;
                priceLast = 0f;
            }

            priceLast = sellLastPrice;
            sellLastPrice = 0f;
            UpdateLastPrice();

            if(buyOrder != null)
            {
                userInfo.RevokeOrder(buyOrder);
            }
        }
    }

    private  void UpdateLastPrice()
    {
        //ToDo
    }
}
