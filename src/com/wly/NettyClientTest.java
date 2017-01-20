package com.wly;

import com.wly.network.NetworkManager;

import io.netty.bootstrap.Bootstrap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/6.
 */
public class NettyClientTest
{
    static public void main(String[] args) throws Exception
    {
        // https://jy.xzsec.com/Login/Authentication
        String strUrl = "https://jy.xzsec.com/Login/Authentication";
        URL url = new URL(strUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
        out.write("userId=1154060016607234&password=12533xxa232&randNumber=&identifyCode=&duration=300&authCode=&type=Z");
        out.flush();
        BufferedReader bufferedReaderHead = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
        String strTmp;
        while((strTmp = bufferedReader.readLine()) != null)
        {
            System.out.println(strTmp);
        }
    }
}
