package com.wly.stock;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/14.
 */
public class StockInfoProviderSimulator implements IStockInfoProvider
{
    private StockInfoProviderSina stockInfoProviderSina;

    public StockInfoProviderSimulator()
    {
        stockInfoProviderSina = new StockInfoProviderSina();
    }
    @Override
    public StockMarketInfo GetStockInfoByCode(String code) throws Exception
    {
        StockMarketInfo stockMarketInfo = stockInfoProviderSina.GetStockInfoByCode(code);
        ModifyMarketInfo(stockMarketInfo);
        return stockMarketInfo;
    }

    @Override
    public ArrayList<StockMarketInfo> GetStockInfoByCode(ArrayList<String> codeList) throws Exception
    {
        ArrayList<StockMarketInfo> stockMarketInfos = stockInfoProviderSina.GetStockInfoByCode(codeList);

        if(stockMarketInfos == null)
        {
            return null;
        }

        int i;
        for(i=0; i<stockMarketInfos.size(); ++i)
        {
            ModifyMarketInfo(stockMarketInfos.get(i));
        }

        return stockMarketInfos;
    }

    private float oldPrice = 0f;
    private int factor = -1;
    private void ModifyMarketInfo(StockMarketInfo stockMarketInfo)
    {
        if (oldPrice <= 0.01f)
        {
            oldPrice = stockMarketInfo.priceNew;
        } else
        {
            //stockMarketInfo.priceNew = oldPrice + priceUnit*0.1f*factor;
            oldPrice = stockMarketInfo.priceNew;

            if(oldPrice >= 110f)
            {
                factor = -1;
            }
            else if(oldPrice <= 60f)
            {
                factor = 1;
            }

            int i;
            for(i=0;i<stockMarketInfo.buyInfo.size();++i)
            {
                stockMarketInfo.buyInfo.get(i).price = stockMarketInfo.priceNew-i*0.01f;
            }

            for(i=0; i<stockMarketInfo.sellInfo.size(); ++i)
            {
                stockMarketInfo.sellInfo.get(i).price = stockMarketInfo.priceNew+(i+1)*0.01f;
            }
        }
    }
}
