package com.wly.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2016/12/6.
 */
public class Acceptor extends ChannelInitializer<SocketChannel>
{
    private ConfigAcceptor m_conf;
    private boolean m_isOpen;

    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public Acceptor(ConfigAcceptor conf)
    {
        m_conf = conf;
        m_isOpen = false;
    }

    public String GetName()
    {
        return m_conf.name;
    }

    public ConfigAcceptor GetConf()
    {
        return m_conf;
    }

    public boolean IsOpen()
    {
        return m_isOpen;
    }

    public void Start()
    {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.localAddress(new InetSocketAddress(m_conf.port));
            serverBootstrap.childHandler(this);
            ChannelFuture channelFuture = serverBootstrap.bind();//.sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess())
                    {
                        System.out.println("Server bind Succ: "+m_conf.port);
                     //   bossGroup.shutdownGracefully();
                     //   workerGroup.shutdownGracefully();
                        m_isOpen = true;
                    }
                    else
                    {
                        channelFuture.cause().printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Close()
    {
        m_isOpen = false;
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {
        System.out.println("Get New Client");
        socketChannel.close();
    }
}
