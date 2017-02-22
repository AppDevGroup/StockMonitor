package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.stock.common.eStockPlate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockUtils
{
    static public ArrayList<String> QueryCodeList = new ArrayList<String>();

    static public final float FeeRate = 0.00025f;
    static public final float ChangeUnit = 0.045f; //上证每100股 0.45
    static public final float StampTaxRate = 0.001f; //交易印花税 交易总额的千分之一

    static public eStockPlate GetPlateByCode(String codeStr)
    {
        eStockPlate plate = eStockPlate.None;
        int code = Integer.parseInt(codeStr);

        int i;
        for (i = 0; i < StockConst.SpecialCodeSH.length; ++i)
        {
            if(StockConst.SpecialCodeSH[i].equals(codeStr))
            {
                plate = eStockPlate.PlateSH;
                return  plate;
            }
        }

        for (i = 0; i < StockConst.SpecialCodeSZ.length; ++i)
        {
            if(StockConst.SpecialCodeSZ[i].equals(codeStr))
            {
                plate = eStockPlate.PlateSZ;
                return  plate;
            }
        }


        if (plate == eStockPlate.None)
        {
            switch (code / 100000)
            {
                case 6:
                case 7:
                    plate = eStockPlate.PlateSH;
                    break;
                case 3:
                case 0:
                    plate = eStockPlate.PlateSZ;
                    break;
            }
        }

        return plate;
    }

    static public  void DoTrade(int policyId, String code, int tradeFlag, float price, int num)
    {
        //buy
        if(tradeFlag == 0)
        {
            DoTradeBuy(policyId, code, price, num);
        }
        else
        {
            DoTradeSell(policyId, code, price, num);
        }
    }

    static  public  void DoTradeSell(int policyId, String code, float price, int num)
    {
        //佣金万分之五（5元起） 上证过户费0.35每100股 向上去整精确到分 印花税千分之一精确到分向上取整
        float amount = price*num;
        float counterFee = GetCountFee(amount, num);    //佣金
        float transferFee = GetTransferFee(code, amount, num);
        float stampTax = GetStampTax(amount);
        float remain = amount-counterFee-transferFee-stampTax;
        LogUtils.Log(String.format("DoTradeBuy %s %.2f %d %.2f %.2f %.2f %.2f\n", code, price, num, amount, counterFee, transferFee, stampTax));
        LogUtils.Log("remain: "+remain);
        final String InsterFormat = "INSERT INTO trade_book (code, trade_flag, price, number, counter_fee, transfer_fee, stamp_tax, time) " +
                "                                  VALUES('%s', 1, %.2f, %d, %.2f, %.2f, %.2f, '%s')";
        String sqlstr = String.format(InsterFormat, code, price, num, counterFee, transferFee, stampTax, Utils.GetTimestampNow().toString());
        DBPool.GetInstance().ExecuteNoQuerySqlAsync (sqlstr);
    }

    static  public  void DoTradeBuy(int policyId, String code, float price, int num)
    {
        //佣金万分之五（5元起）
        float amount = price*num;
        float counterFee = GetCountFee(amount, num);    //佣金
        float transferFee = GetTransferFee(code, amount, num);
        LogUtils.Log(String.format("DoTradeBuy %s %.2f %d %.2f %.2f %.2f\n", code, price, num, amount, counterFee, transferFee));
        LogUtils.Log("cost: "+(amount+counterFee+transferFee));

        final String InsterFormat = "INSERT INTO trade_book (code, trade_flag, price, number, counter_fee, transfer_fee, time) " +
                "                                  VALUES('%s', 0, %.2f, %d, %.2f, %.2f, '%s')";
        String sqlstr = String.format(InsterFormat, code, price, num, counterFee, transferFee, Utils.GetTimestampNow().toString());
        DBPool.GetInstance().ExecuteNoQuerySqlAsync(sqlstr);
    }

    static public float TrimValueFloor(float val)
    {
        return (float)Math.floor((double)(val*100))/100;
    }

    static public float TrimValueRound(float val)
    {
        return (float)Math.round((double)(val*100))/100;
    }

    //佣金计算 万分之五（5元起）
    //买卖都收
    static public float GetCountFee(float amount, int num)
    {
        float counterFee = StockUtils.TrimValueRound(amount*FeeRate);    //佣金
        counterFee = counterFee <= 5f ?5f:counterFee;
        return counterFee;
    }

    ///过户费 上证过户费0.035每100股 向下取整精确到分
    ///买卖都收
    static public float GetTransferFee(String code, float amount, int num)
    {
        float transferFee = 0f;
        eStockPlate plate = StockUtils.GetPlateByCode(code);
        switch (plate)
        {
            case PlateSH:
                transferFee = StockUtils.TrimValueFloor(num/100*ChangeUnit);
                break;
        }
        return transferFee;
    }

    //印花税 交易金额千分之一精确到分向上取整
    //卖出时收取
    static public float GetStampTax(float amount)
    {
        return TrimValueRound(StampTaxRate * amount);
    }
}
