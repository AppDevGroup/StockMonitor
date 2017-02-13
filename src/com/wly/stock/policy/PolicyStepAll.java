package com.wly.stock.policy;

import com.wly.common.Utils;
import com.wly.stock.StockConst;
import com.wly.stock.StockMarketInfo;
import com.wly.stock.StockUtils;
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
            userInfo.DoTrade(code, StockConst.TradeSell, maxPrice, asset.activeCount);
            policyStat = PolicyStat_Init;
            return;
        }

        if(buyTradeInfo.price >= priceSell)
        {
            //doSell
            offset = stockMarketInfo.priceNew - priceLast;
            unitCount = (int)((offset-sellOffset)/priceUnit);
            tradeCount = stepUnit*unitCount >= asset.activeCount?asset.activeCount:stepUnit*unitCount;
            if(stockMarketInfo.TestDeal(StockConst.TradeSell, priceSell, tradeCount))
            {
                userInfo.DoTrade(code, StockConst.TradeSell, priceSell, tradeCount);
                priceLast = priceLast+priceUnit*unitCount;
                //            UpdateLastPrice(priceLast);
            }
        }
        else if(sellTradeInfo.price <= priceBuy)
        {
            //doBuy
            offset = priceLast- stockMarketInfo.priceNew;
            unitCount = (int)((offset+buyOffset)/priceUnit);
            tradeCount = stepUnit*unitCount;
            if(stockMarketInfo.TestDeal(StockConst.TradeBuy, priceBuy, tradeCount))
            {
                userInfo.DoTrade(code, StockConst.TradeBuy, priceBuy, tradeCount);
                priceLast = priceLast - priceUnit * unitCount;
//            UpdateLastPrice(priceLast);
            }
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
}
