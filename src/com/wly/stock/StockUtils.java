package com.wly.stock;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockUtils
{
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
}

enum eStockPlate
{
    None,
    PlateSH,
    PlateSZ,
}
