package com.wly.stock.policy;

import com.mysql.jdbc.Util;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockInfo;
import com.wly.stock.StockUtils;

import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/23.
 */
public class PolicyStep
{
    public int id;
    public  String code;
    public  float priceInit;
    public  float priceUnit;
    public  float priceLast;
    public  int stepUnit;

    public  void PrcessPrice(StockInfo stockInfo)
    {
        float change = stockInfo.priceNew-stockInfo.priceLastDay;
        float changeRatio = change/stockInfo.priceLastDay;
        if(stockInfo.priceNew < 0.1f)
        {
            Utils.Log("error price for :"+stockInfo.code+" price: "+stockInfo.priceNew);
            return;
        }

        System.out.println(String.format("PrcessPrice code:%s  priceLast:%.2f priceUnit:%.2f priceNew:%.2f change:%+.2f changeRatio:%+.2f",
                code, priceLast, priceUnit, stockInfo.priceNew, change, changeRatio*100));
        if(stockInfo.priceNew> priceLast+priceUnit)
        {
            StockUtils.DoTradeSell(id, code, priceLast+priceUnit, stepUnit);
            priceLast = priceLast+priceUnit;
            UpdateLastPrice(priceLast);
        }
        else if(stockInfo.priceNew < priceLast-priceUnit)
        {
            StockUtils.DoTradeBuy(id, code, priceLast-priceUnit, stepUnit);
            priceLast = priceLast-priceUnit;
            UpdateLastPrice(priceLast);
        }
    }

    public  void UpdateLastPrice(float price)
    {
        Utils.Log("UpdateLastPrice:"+code+" "+price);

        if(price< 0.01f)
        {
            return;
        }

        try {
            final String UpdateFormat = "update policy_step SET price_last = %.2f WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, price, id));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    static public HashMap<String, PolicyStep> PolicyStepHashMap = new HashMap<String, PolicyStep>();

    static public  void Init()
    {
        try {
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync("select * from policy_step");
            PolicyStep policyStep;
            ResultSet rs = dbQuery.resultSet;
            while (rs.next()) {
                policyStep = new PolicyStep();
                policyStep.id = rs.getInt(1);
                policyStep.code = rs.getString(2);
                policyStep.priceInit = rs.getFloat(3);
                policyStep.priceUnit = rs.getFloat(4);
                policyStep.priceLast = rs.getFloat(5);
                policyStep.stepUnit = rs.getInt(6);
                StockUtils.QueryCodeList.add(policyStep.code);
                Utils.Log(policyStep.toString());
                PolicyStepHashMap.put(policyStep.code, policyStep);
            }
            //dbMgr.Reset();
            dbQuery.Close();
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }

    public String toString()
    {
       final String StrDescFormat = "PolicyStep: id=%d code=%s priceInit=%.2f priceUnit=%.2f priceLast=%.2f stepUnit=%d ";
        return String.format(StrDescFormat, id, code, priceInit, priceUnit, priceLast, stepUnit);
    }
}
