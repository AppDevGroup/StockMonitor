package com.wly.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import sun.security.ssl.Debug;

import java.net.URI;
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

    static public class TestHttpHandle extends SimpleChannelInboundHandler<HttpObject>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject content) throws Exception
        {
            System.out.println("get response!");
            //ByteBuf buf = content.content();
            //System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
        }
    }

    private void SendHttpRequest(SocketChannel socketChannel)
    {
        try
        {
            URI uri = new URI("http://"+m_conf.adress+"/");
            String msg = "Are you ok?";
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

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
