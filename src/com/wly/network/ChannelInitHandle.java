package com.wly.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by Administrator on 2016/12/6.
 */
public class ChannelInitHandle extends ChannelInitializer<SocketChannel>
{
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            System.out.println("ChanleInitHandle initChannel");
        }
}
