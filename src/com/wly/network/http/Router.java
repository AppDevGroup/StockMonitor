package com.wly.network.http;

import com.wly.common.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by wuly on 2017/5/13.
 */
public class Router extends SimpleChannelInboundHandler<HttpRequest>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) throws Exception
    {
        LogUtils.LogRealtime("channelRead0: "+httpRequest.uri());

    }
}
