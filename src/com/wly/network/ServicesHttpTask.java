package com.wly.network;

import com.wly.network.http.HttpTask;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * Created by wuly on 2017/8/11.
 */
public class ServicesHttpTask
{
    private ExecutorService executorService;
    private PoolingHttpClientConnectionManager cm;
    private CloseableHttpClient httpclient;

    private Queue<HttpTask> httpTasks = new LinkedList<>();

    public void Init(ExecutorService executorService)
    {
        this.executorService = executorService;
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);

        httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }
}
