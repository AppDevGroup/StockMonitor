package com.wly.network;

import io.netty.bootstrap.Bootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

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

            if(m_conf.id == 2)
            {
                SendHttpRequest(socketChannel);
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

    public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("HttpClientInboundHandler channelRead: "+msg.getClass());
        }
    }

    private void SendHttpRequest(SocketChannel socketChannel)
    {
        try
        {
            URI uri = new URI("http://"+m_conf.adress+"/");
            String msg = "Are you ok?";
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    new URI("http://"+m_conf.adress+"/").toASCIIString());//Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

            // 构建http请求
            request.headers().set(HttpHeaders.Names.HOST, m_conf.adress);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
            // 发送http请求
            socketChannel.write(request);
            socketChannel.flush();
            System.out.println("write request!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
