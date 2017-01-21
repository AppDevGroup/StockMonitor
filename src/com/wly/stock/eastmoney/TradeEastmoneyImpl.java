package com.wly.stock.eastmoney;

import com.wly.stock.StockMarketInfo;
import com.wly.stock.common.TradeSeq;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by Administrator on 2017/1/21.
 */
public class TradeEastmoneyImpl
{
    public final String RootUrl = "https://jy.xzsec.com";
    public final String LoginPage = "/Login/Authentication";
    public  final String GetStockList = "/Search/GetStockList";

    private  CookieStore cookieStore;

    //5406001660721212
    public void login(String acct, String psw) throws Exception
    {
        Response response;
        Request request = Request.Post(RootUrl+LoginPage).bodyForm(Form.form().add("userId", acct).
                add("password", psw).add("randNumber", "").
                add("identifyCode", "").add("duration", "30").add("authCode", "").
                add("type", "Z").build());

        Executor executor = Executor.newInstance();
        executor.use(cookieStore)
                .execute(request);

        List<Cookie> cookieList = cookieStore.getCookies();
        int i;
        for(i=0; i<cookieList.size(); ++i)
        {
            System.out.println(cookieList.get(i).getName()+" "+cookieList.get(i).getValue());
        }

        String urlAssets = "https://jy.xzsec.com/Search/GetStockList";
        Request requestAsset = Request.Post(urlAssets);
        response = executor.use(cookieStore)
                .execute(requestAsset);

        System.out.println(response.returnContent());

    }

    public  void doTrade(StockMarketInfo stockMarketInfo, int trade_flag, float price, float count)
    {

    }

    public  void getStockList()
    {

    }

    public List<TradeSeq> getTradeListToday()
    {
        return  null;
    }

    public List<TradeSeq> GetTrandListHis(String dayStart, String dayEnd)
    {
        return null;
    }
}
