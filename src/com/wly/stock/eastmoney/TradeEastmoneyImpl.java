package com.wly.stock.eastmoney;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wly.common.Utils;
import com.wly.stock.StockConst;
import com.wly.stock.common.*;
import com.wly.user.UserInfo;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/1/21.
 */
public class TradeEastmoneyImpl implements ITradeInterface
{
    public final String RootUrl = "https://jy.xzsec.com";
    public final String LoginPage = "/Login/Authentication";
    public final String GetStockList = "/Search/GetStockList";

    private String platUserName;
    private String validatekey;
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
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
            String retStr = Utils.GetResponseContent(response);
            //{"Message":null,"Status":0,"Data":[{"khmc":"张三","Date":"20170209","Time":"142154","Syspm1":"1234545656","Syspm2":"1234","Syspm3":"","Syspm_ex":""}]}
            System.out.println(retStr);
            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("login failed! "+jsonObject.get("Message").getAsString());
                return;
            }

            Cookie cookieTmp = localContext.getCookieStore().getCookies().get(0);
            BasicClientCookie cookie = new BasicClientCookie("eastmoney_txzq_zjzh", URLEncoder.encode(new BASE64Encoder().encode((acct+"|").getBytes())));
            cookie.setPath(cookieTmp.getPath());
            cookie.setDomain(cookieTmp.getDomain());
            cookie.setExpiryDate(cookieTmp.getExpiryDate());
            localContext.getCookieStore().addCookie(cookie);
            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            platUserName = jsonDataArray.get(0).getAsJsonObject().get("khmc").getAsString();
            System.out.println("userName: "+platUserName);

            final String PageBuy = "/Trade/Buy";
            HttpGet httpGet = new HttpGet(RootUrl + PageBuy);
            response = httpclient.execute(httpGet, localContext);
            String pageContent = Utils.GetResponseContent(response);
            //System.out.println("pageContent: "+pageContent);
            final  String FindString = "input id=\"em_validatekey\" type=\"hidden\" value=\"";
            int startIdex = pageContent.indexOf(FindString)+FindString.length();
            validatekey = pageContent.substring(startIdex, startIdex+36);
            System.out.println("validatekey: "+validatekey);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public boolean FillUserAsset(UserInfo userInfo)
    {
        float money = 0;
        try
        {
            final String GetRmbAssetPage = "/Com/GetAssets?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + GetRmbAssetPage+validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("moneyType", "RMB"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // Bind custom cookie store to the local context

            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
            String retStr = Utils.GetResponseContent(response);
            //{"Message":null,"Status":0,"Data":[{"RMBZzc":"1.73","Zzc":"1.73","Zxsz":"0.73","Kyzj":"1.73","Kqzj":"1.00","Djzj":"0.00","Zjye":"1.00","Money_type":"RMB","Drckyk":null,"Ljyk":null}]}

            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("get asset failed! "+jsonObject.get("Message").getAsString());
                return false;
            }
            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            money = jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();
//            System.out.println("userName: "+jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();

            System.out.println(retStr);
            return true;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void DoOrder(OrderInfo orderInfo)
    {
        try
        {
            final String OrderUrl = "/Trade/SubmitTrade?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + OrderUrl+validatekey);

            //stockCode=601288&price=3.00&amount=100&tradeType=B&zqmc=%E5%86%9C%E4%B8%9A%E9%93%B6%E8%A1%8C
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("stockCode", orderInfo.code));
            params.add(new BasicNameValuePair("price", String.format("%.2f",orderInfo.orderPrice)));
            params.add(new BasicNameValuePair("amount", Integer.toString(orderInfo.count)));
            params.add(new BasicNameValuePair("tradeType", orderInfo.tradeFlag== StockConst.TradeBuy?"B":"S"));
            params.add(new BasicNameValuePair("zqmc", orderInfo.name));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
//            System.out.println(Utils.GetResponseFull(response));
            String retStr = Utils.GetResponseContent(response);
            System.out.println("orderResponse: " + retStr);
            //{"Message":null,"Status":0,"Data":[{"Wtbh":"324917"}]}
            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("DoOrder failed! "+jsonObject.get("Message").getAsString());
                return;
            }
////            System.out.println("userName: "+jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();
//
           // System.out.println(retStr);
            return;
        }
        catch (Exception ex)
        {
            System.out.println("ex message: "+ex.getMessage());
            ex.printStackTrace();
        }
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

    public static void enableSSL(DefaultHttpClient httpclient) {

        // 调用ssl

        try {

            SSLContext sslcontext = SSLContext.getInstance("TLS");

           // sslcontext.init(null, new TrustManager[]{truseAllManager}, null);

            @SuppressWarnings("deprecation")

            SSLSocketFactory sf = new SSLSocketFactory(sslcontext);

            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            Scheme https = new Scheme("https", sf, 443);

            httpclient.getConnectionManager().getSchemeRegistry()

                    .register(https);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
