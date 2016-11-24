package com.wly.stock.policy;

import com.mysql.jdbc.Util;
import com.wly.common.Utils;
import com.wly.database.DataBaseManager;
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
       // System.out.println(stockInfo.toString());
        if(stockInfo.priceNew> priceLast+priceUnit)
        {
            StockUtils.DoTradeSell(id, code, stockInfo.priceNew, stepUnit);
            priceLast = priceLast+priceUnit;
            UpdateLastPrice(priceLast);
        }
        else if(stockInfo.priceNew < priceLast-priceUnit)
        {
            StockUtils.DoTradeBuy(id, code, stockInfo.priceNew, stepUnit);
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
            DataBaseManager dbMgr = DataBaseManager.GetInstance();
            final String UpdateFormat = "update policy_step SET price_last = %.2f WHERE id = %d";
            int ret = dbMgr.ExecuteUpdate (String.format(UpdateFormat, price, id));
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
            DataBaseManager dbMgr = DataBaseManager.GetInstance();
            ResultSet rs = dbMgr.ExecuteQuery("select * from policy_step");
            PolicyStep policyStep;
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
            dbMgr.Reset();
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
