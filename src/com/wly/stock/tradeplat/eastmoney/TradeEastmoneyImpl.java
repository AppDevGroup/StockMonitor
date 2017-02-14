package com.wly.stock.tradeplat.eastmoney;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wly.common.Utils;
import com.wly.stock.StockConst;
import com.wly.stock.StockUtils;
import com.wly.stock.common.*;
import com.wly.stock.tradeplat.ITradeInterface;
import com.wly.user.UserInfo;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/1/21.
 */
public class TradeEastmoneyImpl implements ITradeInterface
{
    public final String OrderStat_Order = "已报";
    public final String OrderStat_Cancle = "已撤";
    public final String OrderStat_WaitForCancel = "已报待撤";
    public final String OrderStat_Half = "部成";
    public final String OrderStat_Done = "成交";

    public final float FeeRate = 0.00025f;  //券商交易费率万分之二点五
    public final float ChangeUnit = 0.045f; //上证每100股 0.45
    public final float StampTaxRate = 0.001f; //交易印花税 交易总额的千分之一

    public final String RootUrl = "https://jy.xzsec.com";
    public final String LoginPage = "/Login/Authentication";
    public final String GetStockList = "/Search/GetStockList";

    private UserInfo userInfo;
    private String platUserName;
    private String validatekey;
    private  HttpClientContext localContext;

    public TradeEastmoneyImpl()
    {
        localContext = new HttpClientContext();
        localContext.setCookieStore(new BasicCookieStore());
    }

    @Override
    public void SetUserInfo(UserInfo userInfo)
    {
        this.userInfo = userInfo;
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
    public boolean UpdateUserAsset()
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
            System.out.println(retStr);
            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("get asset failed! "+jsonObject.get("Message").getAsString());
                return false;
            }
            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            userInfo.rmbAsset.activeAmount = jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();

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
            if(!orderInfo.platOrderId.equals(null))
            {
                return;
            }

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

            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            orderInfo.platOrderId = jsonDataArray.get(0).getAsJsonObject().get("Wtbh").getAsString();

//            System.out.println("userName: "+jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();
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
    public void RevokeOrder(OrderInfo orderInfo)
    {
        try
        {
            String date = Utils.GetDate();// new Date()为获取当前系统时间

            final String RevokeUrl = "/Trade/RevokeOrders?validatekey=";
            //"20170213_140266"
            String revokeId = String.format("%s_%s", date, orderInfo.platOrderId);

            HttpPost httpPost = new HttpPost(RootUrl + RevokeUrl + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("revokes", revokeId));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
            String retStr = Utils.GetResponseContent(response);
            System.out.println(retStr);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void UpdateOrderStatus(ArrayList<OrderInfo> orderInfos)
    {
        try
        {
            final String RevokeUrl = "/Search/GetOrdersData?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + RevokeUrl + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("qqhs", "20"));
            params.add(new BasicNameValuePair("wc", ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
            //{"Message":null,"Status":0,"Data":[
            // {"Wtsj":"105843","Zqdm":"601288","Zqmc":"农业银行","Mmsm":"证券买入","Mmlb":"B","Wtsl":"100","Wtzt":"已撤","Wtjg":"3.000","Cjsl":"0","Cjje":".00","Cjjg":"0.000000","Market":"HA","Wtbh":"135153","Gddm":"A296011296","Dwc":"","Qqhs":null,"Wtrq":"20170213","Wtph":"135153","Khdm":"720600166011","Khxm":"张三","Zjzh":"720600166011","Jgbm":"5406","Bpsj":"105843","Cpbm":"","Cpmc":"","Djje":".00","Cdsl":"100","Jyxw":"33392","Cdbs":"F","Czrq":"20170213","Wtqd":"9","Bzxx":"","Sbhtxh":"1430022816","Mmlb_ex":"B","Mmlb_bs":"B"},
            // {"Wtsj":"110528","Zqdm":"601288","Zqmc":"农业银行","Mmsm":"证券买入","Mmlb":"B","Wtsl":"100","Wtzt":"已撤","Wtjg":"3.000","Cjsl":"0","Cjje":".00","Cjjg":"0.000000","Market":"HA","Wtbh":"140266","Gddm":"A296011296","Dwc":"","Qqhs":null,"Wtrq":"20170213","Wtph":"140266","Khdm":"720600166011","Khxm":"张三","Zjzh":"720600166011","Jgbm":"5406","Bpsj":"110528","Cpbm":"","Cpmc":"","Djje":".00","Cdsl":"100","Jyxw":"33392","Cdbs":"F","Czrq":"20170213","Wtqd":"9","Bzxx":"","Sbhtxh":"1430023595","Mmlb_ex":"B","Mmlb_bs":"B"},
            // {"Wtsj":"111724","Zqdm":"601288","Zqmc":"农业银行","Mmsm":"证券买入","Mmlb":"B","Wtsl":"100","Wtzt":"已报","Wtjg":"3.000","Cjsl":"0","Cjje":".00","Cjjg":"0.000000","Market":"HA","Wtbh":"147719","Gddm":"A296011296","Dwc":"20170213|147719","Qqhs":null,"Wtrq":"20170213","Wtph":"147719","Khdm":"720600166011","Khxm":"张三","Zjzh":"720600166011","Jgbm":"5406","Bpsj":"111724","Cpbm":"","Cpmc":"","Djje":"305.01","Cdsl":"0","Jyxw":"33392","Cdbs":"F","Czrq":"20170213","Wtqd":"9","Bzxx":"","Sbhtxh":"1430024755","Mmlb_ex":"B","Mmlb_bs":"B"}
            // ]}
            String retStr = Utils.GetResponseContent(response);
            System.out.println(retStr);

            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("UpdateOrderStatus failed! "+jsonObject.get("Message").getAsString());
                return;
            }

            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            int i,j;
            JsonObject newOrderInfo;
            OrderInfo orderInfo;
            int orderStat;
            boolean needUpdateAsset = false;
            for(i=0; i<jsonDataArray.size(); ++i)
            {
                newOrderInfo = jsonDataArray.get(i).getAsJsonObject();
                orderStat =  GetStatByPlatStat(newOrderInfo.get("Wtzt").getAsString());
                for(j=0; j<orderInfos.size(); ++j)
                {
                    orderInfo = orderInfos.get(j);
                    if(newOrderInfo.get("Wtbh").equals(orderInfo.platOrderId) && orderStat != orderInfo.GetStat())
                    {
                        orderInfo.SetStat(orderStat);
                        if((orderStat == OrderInfo.OderStat_Order && orderInfo.tradeFlag == StockConst.TradeBuy)
                            ||(orderStat == OrderInfo.OderStat_Cancel && orderInfo.tradeFlag == StockConst.TradeBuy)
                                ||(orderStat == OrderInfo.OderStat_Deal && orderInfo.tradeFlag == StockConst.TradeBuy)
                                ||(orderStat == OrderInfo.OderStat_Half && orderInfo.tradeFlag == StockConst.TradeBuy))
                        {
                            needUpdateAsset = true;
                        }
                    }
                }
            }

            if(needUpdateAsset)
            {
                userInfo.UpdateUserAsset();
            }
        }
        catch (Exception ex)
        {
            System.out.print(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public int GetOrderStatus(String platOrderId)
    {
        return 0;
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

    @Override
    public float CacuTradeFee(int tradeFlag, String code, float price, int count)
    {
        float tradeFeeTotal = 0f;

        switch (tradeFlag)
        {
            case StockConst.TradeBuy:
                tradeFeeTotal = CacuBuyFee(code, price, count);
                break;
            case StockConst.TradeSell:
                tradeFeeTotal = CacuSellFee(code, price, count);
        }
        return tradeFeeTotal;
    }

    private int GetStatByPlatStat(String str)
    {
        int stat = OrderInfo.OderStat_None;
        switch (str)
        {
            case OrderStat_Order:
                stat = OrderInfo.OderStat_Order;
                break;
            case OrderStat_Cancle:
                stat = OrderInfo.OderStat_Cancel;
                break;
            case OrderStat_Half:
                stat = OrderInfo.OderStat_Half;
                break;
            case OrderStat_WaitForCancel:
                stat = OrderInfo.OderStat_WaitForCancel;
                break;
            case OrderStat_Done:
                stat = OrderInfo.OderStat_Deal;
                break;
        }
        return  stat;
    }

    public  float CacuSellFee(String code, float price, int num)
    {
        //佣金万分之五（5元起） 上证过户费0.35每100股 向上去整精确到分 印花税千分之一精确到分向上取整
        float amount = price*num;
        float counterFee = GetCountFee(amount, num);    //佣金
        float transferFee = GetTransferFee(code, amount, num);
        float stampTax = GetStampTax(amount);
        return counterFee+transferFee+stampTax;
    }

    public  float CacuBuyFee(String code, float price, int num)
    {
        //佣金万分之五（5元起）
        float amount = price*num;
        float counterFee = GetCountFee(amount, num);    //佣金
        float transferFee = GetTransferFee(code, amount, num);
        return counterFee+transferFee;
    }

    static public float TrimValueFloor(float val)
    {
        return (float)Math.floor((double)(val*100))/100;
    }

    static public float TrimValueRound(float val)
    {
        return (float)Math.round((double)(val*100))/100;
    }

    //佣金计算 万分之五（5元起）
    //买卖都收
    public float GetCountFee(float amount, int num)
    {
        float counterFee = StockUtils.TrimValueRound(amount*FeeRate);    //佣金
        counterFee = counterFee <= 5f ?5f:counterFee;
        return counterFee;
    }

    ///过户费 上证过户费0.035每100股 向下取整精确到分
    ///买卖都收
    public float GetTransferFee(String code, float amount, int num)
    {
        float transferFee = 0f;
        eStockPlate plate = StockUtils.GetPlateByCode(code);
        switch (plate)
        {
            case PlateSH:
                transferFee = StockUtils.TrimValueFloor(num/100*ChangeUnit);
                break;
        }
        return transferFee;
    }

    //印花税 交易金额千分之一精确到分向上取整
    //卖出时收取
    public float GetStampTax(float amount)
    {
        return TrimValueRound(StampTaxRate * amount);
    }
}
