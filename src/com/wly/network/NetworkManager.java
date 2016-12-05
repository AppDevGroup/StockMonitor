package com.wly.network;

import com.wly.NettyServerTest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2016/12/5.
 */
public class NetworkManager
{
    private static NetworkManager s_instance = null;

    static public NetworkManager GetInstance()
    {
        if(s_instance == null)
        {
            s_instance = new NetworkManager();
        }

        return s_instance;
    }

    public void Init(String config)
    {

    }

    public void StartAcceptor(ConfigServer conf)
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.localAddress(new InetSocketAddress(conf.port));
            serverBootstrap.childHandler(new NettyServerTest.ChannelInitHandle());
            ChannelFuture cf = serverBootstrap.bind().sync();
            System.out.println("listen on: "+conf.port);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public  void StartConnector(ConfigClient conf)
    {

    }
}
