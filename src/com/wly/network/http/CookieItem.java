package com.wly.network.http;

import com.google.gson.JsonArray;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import sun.misc.BASE64Encoder;

import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by wuly on 2017/8/11.
 */
public class CookieItem
{
    public String path;
    public String domain;
    public String key;
    public String value;
    public Date expireDate;

    public BasicClientCookie GetCookie()
    {
         BasicClientCookie cookie = new BasicClientCookie(key, value);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setExpiryDate(expireDate);
        return cookie;
    }
}
