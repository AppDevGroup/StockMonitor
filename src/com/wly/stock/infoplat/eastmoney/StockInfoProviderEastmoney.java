package com.wly.stock.infoplat.eastmoney;

import com.wly.stock.common.StockMarketInfo;
import com.wly.stock.infoplat.IStockInfoProvider;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/25.
 */
public class StockInfoProviderEastmoney implements IStockInfoProvider
{
    @Override
    public StockMarketInfo GetStockInfoByCode(String code) throws Exception
    {
        return null;
    }

    @Override
    public ArrayList<StockMarketInfo> GetStockInfoByCode(ArrayList<String> codeList) throws Exception
    {
        return null;
    }
}
