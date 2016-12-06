package com.wly.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * Created by Administrator on 2016/12/6.
 */
public class Connector extends ChannelInitializer<SocketChannel>
{
    private ConfigConnector m_conf;
    private boolean m_isConnect;

    public Connector(ConfigConnector conf)
    {
        m_conf = conf;
        m_isConnect = false;
    }

    public String GetName()
    {
        return m_conf.name;
    }

    public ConfigConnector GetConf()
    {
        return m_conf;
    }

    public boolean IsConnect()
    {
        return m_isConnect;
    }

    public void Start()
    {
        try {
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup workGroup = new NioEventLoopGroup();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(this);
            bootstrap.connect(m_conf.adress, m_conf.port).sync();
            System.out.println("connect succ: "+m_conf.adress+" "+m_conf.port);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {
        System.out.println("Connector initChannel");
        ChannelPipeline cp = socketChannel.pipeline();
        try
        {
            for (String clsName : m_conf.handleList)
            {
                Class cls = Class.forName(clsName);
                cp.addLast((ChannelHandler) cls.newInstance());
            }
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException: "+ex.getMessage());
        }
    }

    static public class TestHandle extends SimpleChannelInboundHandler<String>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception
        {
            System.out.println("handle message: "+s);
        }
    }
}
