package com.wly.stock.infoplat.eastmoney;

import com.wly.stock.common.StockMarketInfo;
import com.wly.stock.infoplat.IStockInfoProvider;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/25.
 */
public class StockInfoProviderEastmoney implements IStockInfoProvider
{
    final String Token = "beb0a0047196124721f56b0f0ff5a27c";

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

    //query stock by stock or first letter http://suggest.eastmoney.com/suggest/default.aspx?name=sData&input=jlt&type=
    //query stock realtime info http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=6008681&token=beb0a0047196124721f56b0f0ff5a27c
    //query trade info: http://nufm2.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=6008681&sty=DPTTFD&token=beb0a0047196124721f56b0f0ff5a27c
}
