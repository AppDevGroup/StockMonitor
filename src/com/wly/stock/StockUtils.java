package com.wly.stock;

import com.wly.common.Utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockUtils
{
    static public ArrayList<Integer> QueryCodeList = new ArrayList<Integer>();

    static public String PrefixSH = "sh";
    static public String PrefixSZ = "sz";
    static public  eStockPlate GetPlateByCode(int code)
    {
        eStockPlate plate = eStockPlate.None;
        switch (code/100000)
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

        return  plate;
    }

    static  public  void DoTradeSell(int policyId, int code, float price, int num)
    {
        Utils.Log(String.format("DoTradeSell %d %.2f %d\n", code, price, num));
    }

    static  public  void DoTradeBuy(int policyId,int code, float price, int num)
    {
        Utils.Log(String.format("DoTradeBuy %d %.2f %d\n", code, price, num));
    }
}

enum eStockPlate
{
    None,
    PlateSH,
    PlateSZ,
}
