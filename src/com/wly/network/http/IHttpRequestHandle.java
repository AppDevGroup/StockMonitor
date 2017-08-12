package com.wly.network.http;

import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Created by wuly on 2017/8/11.
 */
public interface IHttpRequestHandle
{
    void HandleHttpResponse(int stat, HttpTask task, CloseableHttpResponse closeableHttpResponse);
}
