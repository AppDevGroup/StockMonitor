package com.wly.network;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */
public class HttpClient
{

    static public  void main(String[] args) throws Exception
    {
        Response response;
        CookieStore cookieStore = new BasicCookieStore();
        String url = "https://jy.xzsec.com/Login/Authentication";
        HttpClientContext context = HttpClientContext.create();
        Request request = Request.Post(url).bodyForm(Form.form().add("userId", "5406001660721212").
                add("password", "1112s5332x").add("randNumber", "").
                add("identifyCode", "").add("duration", "30").add("authCode", "").
                add("type", "Z").build());

        Executor executor = Executor.newInstance();
        executor.use(cookieStore)
                .execute(request);

        //Response response = request.execute();
       // System.out.println(response.returnContent());
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
}
