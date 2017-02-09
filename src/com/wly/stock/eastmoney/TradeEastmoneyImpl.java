package com.wly.stock.eastmoney;

import com.wly.common.Utils;
import com.wly.stock.StockMarketInfo;
import com.wly.stock.common.*;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/21.
 */
public class TradeEastmoneyImpl implements ITradeInterface
{
    public final String RootUrl = "https://jy.xzsec.com";
    public final String LoginPage = "/Login/Authentication";
    public final String GetStockList = "/Search/GetStockList";

    private  HttpClientContext localContext;

    public TradeEastmoneyImpl()
    {
        localContext = new HttpClientContext();
        localContext.setCookieStore(new BasicCookieStore());
    }

    //5406001660721212
    public void Login(String acct, String psw)
    {
        try
        {
            HttpPost httpPost = new HttpPost(RootUrl + LoginPage);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", acct));
            params.add(new BasicNameValuePair("password", psw));
            params.add(new BasicNameValuePair("randNumber", ""));
            params.add(new BasicNameValuePair("identifyCode", ""));
            params.add(new BasicNameValuePair("duration", "30"));
            params.add(new BasicNameValuePair("authCode", ""));
            params.add(new BasicNameValuePair("type", "Z"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // Bind custom cookie store to the local context

            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
            System.out.println(Utils.GetResponseFull(response));

            String urlAssets = "https://jy.xzsec.com/Search/GetStockList";
            httpPost = new HttpPost(urlAssets);
            response = httpclient.execute(httpPost, localContext);
            System.out.println(Utils.GetResponseFull(response));
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public float GetRmbAsset()
    {
        return 0;
    }

    @Override
    public void DoTrade(OrderInfo orderInfo)
    {

    }

    @Override
    public boolean CheckOrderState(OrderInfo orderInfo)
    {
        return false;
    }

    @Override
    public List<TradeBook> GetTradeHis()
    {
        return null;
    }

    @Override
    public List<StockAsset> GetAssetList()
    {
        return null;
    }
}
