package com.wly.stock.infoplat;

import com.wly.stock.common.StockRuntimeInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public interface IStockInfoProvider
{
    StockRuntimeInfo GetStockInfoByCode(String code) throws Exception;
    ArrayList<StockRuntimeInfo> GetStockInfoByCode(ArrayList<String> codeList) throws Exception;
}
