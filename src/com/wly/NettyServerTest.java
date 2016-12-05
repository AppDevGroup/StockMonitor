package com.wly;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2016/12/5.
 */
public class NettyServerTest
{
    static public void main(String[] args)
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.localAddress(new InetSocketAddress(3344));// 设置监听端口
            serverBootstrap.childHandler(new ChannelInitHandle());
            ChannelFuture cf = serverBootstrap.bind().sync();
            System.out.println("listen on 3344");
            PrintCurentThreadId();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static public class ChannelInitHandle extends ChannelInitializer<SocketChannel>
    {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            System.out.println("ChanleInitHandle initChannel");
            PrintCurentThreadId();
        }
    }

    static public  void PrintCurentThreadId()
    {
        System.out.println("Thread Id: "+Thread.currentThread().getId());
    }
}