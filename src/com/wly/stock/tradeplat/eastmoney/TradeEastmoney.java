package com.wly.stock.tradeplat.eastmoney;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wly.common.Utils;
import com.wly.network.http.CookieItem;
import com.wly.network.http.HttpTask;
import com.wly.network.http.IHttpRequestHandle;
import com.wly.stock.StockContext;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockAsset;
import com.wly.stock.common.StockConst;
import com.wly.stock.service.ServiceStockTrade;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuly on 2017/8/11.
 */
public class TradeEastmoney implements IHttpRequestHandle
{
    static public final int TaskGetRmbAsset     = 0;
    static public final int TaskGetStockAsset   = 1;
    static public final int TaskOrderRequest    = 2;
    static public final int TaskQueryOrderStat  = 3;
    static public final int TaskCancelOrder     = 4;

    public final String RootUrl = "https://jy.xzsec.com";

    private HttpClientContext localContext;
    private String validatekey;

    private float rmb = 0f;
    private HashMap<String, StockAsset> stockAssetHashMap = new HashMap<>();

    public TradeEastmoney()
    {
        localContext = new HttpClientContext();
        localContext.setCookieStore(new BasicCookieStore());
    }

    public void AddCookie(ArrayList<CookieItem> cookieList)
    {
        int i;
        for (i = 0; i < cookieList.size(); ++i)
        {
            localContext.getCookieStore().addCookie(cookieList.get(i).GetCookie());
        }
    }

    public void SetValidatekey(String validatekey)
    {
        this.validatekey = validatekey;
    }

    public void Init()
    {

    }

    public float GetRmbAsset()
    {
        return rmb;
    }

    public StockAsset GetStockCountActive(String code)
    {
        return stockAssetHashMap.get(code);
    }

    @Override
    public void HandleHttpResponse(int stat, HttpTask task, CloseableHttpResponse closeableHttpResponse)
    {
        if (stat == HttpTask.StatFailed)
        {
            return;
        }

        switch (task.id)
        {
            case TaskGetRmbAsset:
                OnGetRmbAsset(stat, task, closeableHttpResponse);
                break;
            case TaskGetStockAsset:
                OnGetStockAsset(stat, task, closeableHttpResponse);
                break;
            case TaskOrderRequest:
                OnOrderRequest(stat, task, closeableHttpResponse);
                break;
            case TaskQueryOrderStat:
                OnQueryOrderStat(stat, task, closeableHttpResponse);
                break;
            case TaskCancelOrder:
                OnCancelOrder(stat, task, closeableHttpResponse);
                break;
        }
    }

    public void DoGetRmbAsset()
    {
        try
        {
            final String GetRmbAssetPage = "/Com/GetAssets?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + GetRmbAssetPage + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("moneyType", "RMB"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpTask httpTask = new HttpTask(TaskGetRmbAsset, HttpClients.createDefault(), this);
            httpTask.httpUriRequest = httpPost;
            httpTask.httpClientContext = localContext;
            StockContext.GetInstance().GetExecutorService().execute(httpTask);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void OnGetRmbAsset(int stat, HttpTask task, CloseableHttpResponse response)
    {
        if (stat == HttpTask.StatFailed)
        {
            return;
        }
        String retStr = Utils.GetResponseContent(response);
        //{"Message":null,"Status":0,"Data":[{"RMBZzc":"1.73","Zzc":"1.73","Zxsz":"0.73","Kyzj":"1.73","Kqzj":"1.00","Djzj":"0.00","Zjye":"1.00","Money_type":"RMB","Drckyk":null,"Ljyk":null}]}
        System.out.println(retStr);
        JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
        int code = jsonObject.get("Status").getAsInt();
        if (code != 0)
        {
            System.out.println("get asset failed! " + jsonObject.get("Message").getAsString());
            return;
        }
        JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
        rmb = jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();
    }

    public void DoGetStockAsset()
    {
        try
        {
            final String RevokeUrl = "/Search/GetStockList?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + RevokeUrl + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("qqhs", "20"));
            params.add(new BasicNameValuePair("wc", ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpTask httpTask = new HttpTask(TaskGetStockAsset, HttpClients.createDefault(), this);
            httpTask.httpUriRequest = httpPost;
            httpTask.httpClientContext = localContext;
            StockContext.GetInstance().GetExecutorService().execute(httpTask);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void OnGetStockAsset(int code, HttpTask task, CloseableHttpResponse response)
    {
        try
        {
            /*{"Message":null,"Status":0,"Data":[
                {"Zqdm":"603960","Zqmc":"克来机电","Zqsl":"500","Zqlx":"0","Kysl":"0","Djsl":"0","Cbjg":"48.523","Zxjg":"50.870","Ykbl":"0.048369","Ljyk":"1173.45","Zxsz":"25435.00","Dwc":"A29601129654061603960","Qqhs":null,"Cwbl":"0.61765","Bz":"RMB","Khdm":"540600166725","Gddm":"A296011296","Market":"HA","Zccsl":null}
                ]}
            */
            String retStr = Utils.GetResponseContent(response);
            // System.out.println(retStr);

            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if (stat != 0)
            {
                System.out.println("GetStockAssetCount failed! " + jsonObject.get("Message").getAsString());
                return;
            }

            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            int i;
            JsonObject newOrderInfo;
            StockAsset stockAsset;
            for (i = 0; i < jsonDataArray.size(); ++i)
            {
                stockAsset = new StockAsset();
                newOrderInfo = jsonDataArray.get(i).getAsJsonObject();
                stockAsset.code = newOrderInfo.get("Zqdm").getAsString();
                stockAsset.name = newOrderInfo.get("Zqmc").getAsString();
                stockAsset.amountTotal = newOrderInfo.get("Zqsl").getAsInt();
                stockAsset.amountActive = newOrderInfo.get("Kysl").getAsInt();
                stockAssetHashMap.put(stockAsset.code, stockAsset);
            }
        } catch (Exception ex)
        {
            System.out.print(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void DoOrderRequest(OrderInfo orderInfo)
    {
        //TaskOrderRequest
        try
        {
            if (orderInfo.platOrderId != null && !orderInfo.platOrderId.equals("0"))
            {
                return;
            }

            final String OrderUrl = "/Trade/SubmitTrade?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + OrderUrl + validatekey);

            //stockCode=601288&price=3.00&amount=100&tradeType=B&zqmc=%E5%86%9C%E4%B8%9A%E9%93%B6%E8%A1%8C
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("stockCode", orderInfo.code));
            params.add(new BasicNameValuePair("price", String.format("%.2f", orderInfo.orderPrice)));
            params.add(new BasicNameValuePair("amount", Integer.toString(orderInfo.count)));
            params.add(new BasicNameValuePair("tradeType", orderInfo.tradeFlag == StockConst.TradeBuy ? "B" : "S"));
            params.add(new BasicNameValuePair("zqmc", orderInfo.name));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpTask httpTask = new HttpTask(TaskOrderRequest, HttpClients.createDefault(), this);
            httpTask.httpUriRequest = httpPost;
            httpTask.httpClientContext = localContext;
            httpTask.param = orderInfo;
            StockContext.GetInstance().GetExecutorService().execute(httpTask);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public void OnOrderRequest(int code, HttpTask task, CloseableHttpResponse response)
    {
        String retStr = Utils.GetResponseContent(response);
        System.out.println("orderResponse: " + retStr);
        OrderInfo orderInfo = (OrderInfo) task.param;
        //{"Message":null,"Status":0,"Data":[{"Wtbh":"324917"}]}
        JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
        int stat = jsonObject.get("Status").getAsInt();
        if (stat != 0)
        {
            orderInfo.SetStat(OrderInfo.OderStat_None);
            System.out.println("DoOrder failed! " + jsonObject.get("Message").getAsString());
            return;
        }

        JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
        orderInfo.platOrderId = jsonDataArray.get(0).getAsJsonObject().get("Wtbh").getAsString();
        ServiceStockTrade.GetInstance().OnOrderRequest(orderInfo, orderInfo.platOrderId);
    }

    public void DoQueryOrderStat(OrderInfo orderInfo)
    {
        //TaskQueryOrderStat
        try
        {
            final String RevokeUrl = "/Search/GetOrdersData?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + RevokeUrl + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("qqhs", "20"));
            params.add(new BasicNameValuePair("wc", ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpTask httpTask = new HttpTask(TaskQueryOrderStat, HttpClients.createDefault(), this);
            httpTask.httpUriRequest = httpPost;
            httpTask.httpClientContext = localContext;
            httpTask.param = orderInfo;
            StockContext.GetInstance().GetExecutorService().execute(httpTask);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public void OnQueryOrderStat(int code, HttpTask task, CloseableHttpResponse response)
    {
        //{"Message":null,"Status":0,"Data":[
        // {"Wtsj":"105843","Zqdm":"601288","Zqmc":"农业银行","Mmsm":"证券买入","Mmlb":"B","Wtsl":"100","Wtzt":"已撤","Wtjg":"3.000","Cjsl":"0","Cjje":".00","Cjjg":"0.000000","Market":"HA","Wtbh":"135153","Gddm":"A296011296","Dwc":"","Qqhs":null,"Wtrq":"20170213","Wtph":"135153","Khdm":"720600166011","Khxm":"张三","Zjzh":"720600166011","Jgbm":"5406","Bpsj":"105843","Cpbm":"","Cpmc":"","Djje":".00","Cdsl":"100","Jyxw":"33392","Cdbs":"F","Czrq":"20170213","Wtqd":"9","Bzxx":"","Sbhtxh":"1430022816","Mmlb_ex":"B","Mmlb_bs":"B"},
        // {"Wtsj":"110528","Zqdm":"601288","Zqmc":"农业银行","Mmsm":"证券买入","Mmlb":"B","Wtsl":"100","Wtzt":"已撤","Wtjg":"3.000","Cjsl":"0","Cjje":".00","Cjjg":"0.000000","Market":"HA","Wtbh":"140266","Gddm":"A296011296","Dwc":"","Qqhs":null,"Wtrq":"20170213","Wtph":"140266","Khdm":"720600166011","Khxm":"张三","Zjzh":"720600166011","Jgbm":"5406","Bpsj":"110528","Cpbm":"","Cpmc":"","Djje":".00","Cdsl":"100","Jyxw":"33392","Cdbs":"F","Czrq":"20170213","Wtqd":"9","Bzxx":"","Sbhtxh":"1430023595","Mmlb_ex":"B","Mmlb_bs":"B"},
        // {"Wtsj":"111724","Zqdm":"601288","Zqmc":"农业银行","Mmsm":"证券买入","Mmlb":"B","Wtsl":"100","Wtzt":"已报","Wtjg":"3.000","Cjsl":"0","Cjje":".00","Cjjg":"0.000000","Market":"HA","Wtbh":"147719","Gddm":"A296011296","Dwc":"20170213|147719","Qqhs":null,"Wtrq":"20170213","Wtph":"147719","Khdm":"720600166011","Khxm":"张三","Zjzh":"720600166011","Jgbm":"5406","Bpsj":"111724","Cpbm":"","Cpmc":"","Djje":"305.01","Cdsl":"0","Jyxw":"33392","Cdbs":"F","Czrq":"20170213","Wtqd":"9","Bzxx":"","Sbhtxh":"1430024755","Mmlb_ex":"B","Mmlb_bs":"B"}
        // ]}
        String retStr = Utils.GetResponseContent(response);
        // System.out.println(retStr);

        JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
        int stat = jsonObject.get("Status").getAsInt();
        if (stat != 0)
        {
            System.out.println("UpdateOrderStatus failed! " + jsonObject.get("Message").getAsString());
            return;
        }

        JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
        int i;
        JsonObject newOrderInfo;
        int orderStat;
        String eastMoneyId;
        for (i = 0; i < jsonDataArray.size(); ++i)
        {
            newOrderInfo = jsonDataArray.get(i).getAsJsonObject();
            eastMoneyId = newOrderInfo.get("Wtbh").getAsString();
            orderStat = EastmoneyUtils.GetStatByPlatStat(newOrderInfo.get("Wtzt").getAsString());
            ServiceStockTrade.GetInstance().OnStockStat(eastMoneyId, orderStat);
        }
    }

    public void DoCancelOrder(String platOrderId)
    {
        try
        {
            String date = Utils.GetDate();// new Date()为获取当前系统时间
            final String RevokeUrl = "/Trade/RevokeOrders?validatekey=";
            //"20170213_140266RevokeUrl"
            String revokeId = String.format("%s_%s", date, platOrderId);

            HttpPost httpPost = new HttpPost(RootUrl + RevokeUrl + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("revokes", revokeId));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpTask httpTask = new HttpTask(TaskCancelOrder, HttpClients.createDefault(), this);
            httpTask.httpUriRequest = httpPost;
            httpTask.httpClientContext = localContext;
            StockContext.GetInstance().GetExecutorService().execute(httpTask);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public void OnCancelOrder(int code, HttpTask task, CloseableHttpResponse response)
    {
        String retStr = Utils.GetResponseContent(response);
        System.out.println(retStr);
    }
}
