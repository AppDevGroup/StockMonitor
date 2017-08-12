package com.wly.stock.infoplat.simulate;

import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.infoplat.IStockInfoProvider;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;

import java.util.ArrayList;
import java.util.Random;

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
    public StockRuntimeInfo GetStockInfoByCode(String code) throws Exception
    {
        StockRuntimeInfo stockMarketInfo = stockInfoProviderSina.GetStockInfoByCode(code);
        ModifyMarketInfo(stockMarketInfo);
        return stockMarketInfo;
    }

    @Override
    public ArrayList<StockRuntimeInfo> GetStockInfoByCode(ArrayList<String> codeList) throws Exception
    {
        ArrayList<StockRuntimeInfo> stockMarketInfos = stockInfoProviderSina.GetStockInfoByCode(codeList);

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

    private void ModifyMarketInfo(StockRuntimeInfo stockMarketInfo)
    {
        stockMarketInfo.priceLast = stockMarketInfo.priceNew;
        Random random = new Random();
        int ranVal = random.nextInt(2000);
        stockMarketInfo.priceNew = stockMarketInfo.priceNew * (1 + (ranVal - 1000f) / 10000f);

        int i;
        for (i = 0; i < stockMarketInfo.buyInfo.size(); ++i)
        {
            stockMarketInfo.buyInfo.get(i).price = stockMarketInfo.priceNew - i * 0.01f;
        }

        for (i = 0; i < stockMarketInfo.sellInfo.size(); ++i)
        {
            stockMarketInfo.sellInfo.get(i).price = stockMarketInfo.priceNew + (i + 1) * 0.01f;
        }
    }
}
