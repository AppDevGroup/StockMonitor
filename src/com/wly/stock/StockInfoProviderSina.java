package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.eStockPlate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public class StockInfoProviderSina implements IStockInfoProvider
{
    public enum eInfoIdx
    {
        Name,
        PriceInit,
        PriceLastDay,
        PriceNew,
        PriceMax,
        PriceMin,
        PriceBuy,
        PriceSell,
        TradeTotalCount,
        TradeTotalMoney,
        TradeInfoStart,
    }

    private  static final int TradeInfoCount = 5;

    @Override
    public StockMarketInfo GetStockInfoByCode(String code) throws Exception
    {
        ArrayList<String> queryList = new ArrayList<>();
        queryList.add(code);
        ArrayList<StockMarketInfo> retList = GetStockInfoByCode(queryList);
        return  retList.get(0);
    }

    @Override
    public ArrayList<StockMarketInfo> GetStockInfoByCode(ArrayList<String> codeList) throws Exception
    {
        if(codeList == null || codeList.size()==0)
        {
            LogUtils.GetLogger(LogUtils.LOG_CONSOLE).warn("not code for query!");
            return null;
        }

        StringBuilder sb = new StringBuilder("http://hq.sinajs.cn/list=");
        int i;
        String code;
        String prefix;
        eStockPlate plate = eStockPlate.None;
        for(i=0; i<codeList.size(); ++i)
        {
            code = codeList.get(i);
            plate = StockUtils.GetPlateByCode(code);
            prefix = null;
            switch (plate)
            {
                case PlateSH:
                    prefix = StockConst.PrefixSH;
                    break;
                case PlateSZ:
                    prefix = StockConst.PrefixSZ;
                    break;
            }

            if(prefix == null)
            {
                LogUtils.Log("ignore unknow stock "+code);
                continue;
            }

            if(i==0)
            {
                sb.append(prefix+code);
            }
            else
            {
                sb.append(","+prefix+code);
            }
        }

        URL url = new URL(sb.toString());
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "GB2312"));
        sb.delete( 0, sb.length() );
        //string
        String strTmp;
        ArrayList<StockMarketInfo> infoList = new ArrayList<StockMarketInfo>();
        while((strTmp = bufferedReader.readLine()) != null)
        {
            infoList.add(GetStockInfoByString(strTmp));
        }
        return infoList;
    }

    ///var hq_str_sh603020="爱普股份,22.820,22.790,23.380,23.440,22.710,23.370,23.380,6680397,154655134.000,5200,23.370,8000,23.360,9700,23.350,1000,
    /// 23.340,4300,23.330,43600,23.380,6900,23.400,6100,23.410,14300,23.420,17900,23.430,2016-11-22,14:44:09,00";
    static  public StockMarketInfo GetStockInfoByString(String str)
    {
        //Utils.Log(str);
        String[] strListTmp;
        String strTmp;
        strListTmp = str.split("=");
        if(strListTmp[1].length() <= 3)
        {
            LogUtils.GetLogger(LogUtils.LOG_CONSOLE).error("error info: "+str);
            return null;
        }

        strTmp = strListTmp[0];
        String codeInfo = strTmp.substring(11);
        String code = codeInfo.substring(2);
        String[] infoList = strListTmp[1].substring(1, strListTmp[1].length()-1).split(",");
       // Utils.Log("get info  : "+code+" "+infoList[0]+" "+infoList[3]);

        StockMarketInfo info = new StockMarketInfo();
        info.code = code;
        info.name = infoList[eInfoIdx.Name.ordinal()];
        info.priceInit = Float.parseFloat(infoList[eInfoIdx.PriceInit.ordinal()]);
        info.priceLast = Float.parseFloat(infoList[eInfoIdx.PriceLastDay.ordinal()]);
        info.priceNew = Float.parseFloat(infoList[eInfoIdx.PriceNew.ordinal()]);
        info.priceMax =  Float.parseFloat(infoList[eInfoIdx.PriceMax.ordinal()]);
        info.priceMin =  Float.parseFloat(infoList[eInfoIdx.PriceMin.ordinal()]);
        info.priceBuy = Float.parseFloat(infoList[eInfoIdx.PriceBuy.ordinal()]);
        info.priceSell = Float.parseFloat(infoList[eInfoIdx.PriceSell.ordinal()]);
        info.tradeCount = Long.parseLong(infoList[eInfoIdx.TradeTotalCount.ordinal()]);
        info.tradeMoney = Float.parseFloat(infoList[eInfoIdx.TradeTotalMoney.ordinal()]);

        int i;
        StockMarketInfo.TradeInfo tradeInfo;
        int startIdx = eInfoIdx.TradeInfoStart.ordinal();
        for(i=0; i<TradeInfoCount; ++i)
        {
            tradeInfo = new StockMarketInfo.TradeInfo();
            tradeInfo.amount = Long.parseLong(infoList[startIdx+2*i]);
            tradeInfo.price = Float.parseFloat(infoList[startIdx+2*i+1]);
            info.buyInfo.add(tradeInfo);
        }

        startIdx = eInfoIdx.TradeInfoStart.ordinal()+2*TradeInfoCount;
        for(i=0; i<TradeInfoCount; ++i)
        {
            tradeInfo = new StockMarketInfo.TradeInfo();
            tradeInfo.amount = Long.parseLong(infoList[startIdx+2*i]);
            tradeInfo.price = Float.parseFloat(infoList[startIdx+2*i+1]);
            info.sellInfo.add(tradeInfo);
        }
        //System.out.println(info.toString());
        info.CacuStat();
        return  info;
    }
}
