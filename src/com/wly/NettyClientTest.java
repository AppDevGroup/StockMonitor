package com.wly;

import com.wly.network.NetworkManager;

/**
 * Created by Administrator on 2016/12/6.
 */
public class NettyClientTest
{
    static public void main(String[] args)
    {
        NetworkManager.GetInstance().Init("./config/network_connector.xml");
    }
}
