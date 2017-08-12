package com.wly.network.http;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by wuly on 2017/8/11.
 */

public class HttpTask implements Runnable
{
    static public final int StatSucc = 0;
    static public final int StatFailed = 1;

    public int id;
    public HttpUriRequest httpUriRequest;
    public HttpClientContext httpClientContext;
    public CloseableHttpClient httpclient;
    public IHttpRequestHandle httpRequestHandle;
    public Object param;

    public HttpTask(int id, CloseableHttpClient httpclient, IHttpRequestHandle httpRequestHandle)
    {
        this.id = id;
        this.httpclient = httpclient;
        this.httpRequestHandle = httpRequestHandle;
    }

    @Override
    public void run()
    {
        try
        {
            CloseableHttpResponse response = httpclient.execute(httpUriRequest, httpClientContext);
            if(httpRequestHandle != null)
            {
                httpRequestHandle.HandleHttpResponse(StatSucc, this, response);
            }
            response.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            httpRequestHandle.HandleHttpResponse(StatFailed, this, null);
        }
    }
}
