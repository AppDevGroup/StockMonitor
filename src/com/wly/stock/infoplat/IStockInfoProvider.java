package com.wly.stock.infoplat;

import com.wly.stock.common.StockMarketInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public interface IStockInfoProvider
{
    StockMarketInfo GetStockInfoByCode(String code) throws Exception;
    ArrayList<StockMarketInfo> GetStockInfoByCode(ArrayList<String> codeList) throws Exception;
}
