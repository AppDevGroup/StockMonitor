package com.wly.stock;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public interface IStockInfoProvider
{
    StockInfo GetStockInfoByCode(int code) throws Exception;
    ArrayList<StockInfo> GetStockInfoByCode(ArrayList<Integer> codeList) throws Exception;
}
