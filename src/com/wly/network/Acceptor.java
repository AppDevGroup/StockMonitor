package com.wly.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2016/12/6.
 */
public class Acceptor extends ChannelInitializer<SocketChannel>
{
    private ConfigAcceptor m_conf;
    private boolean m_isOpen;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

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
            serverBootstrap.bind().sync();
            System.out.println("Server bind Succ: "+m_conf.port);
//            ChannelFuture channelFuture = serverBootstrap.bind();
//            channelFuture.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if(channelFuture.isSuccess())
//                    {
//                        System.out.println("Server bind Succ: "+m_conf.port);
//                     //   bossGroup.shutdownGracefully();
//                     //   workerGroup.shutdownGracefully();
//                        m_isOpen = true;
//                    }
//                    else
//                    {
//                        channelFuture.cause().printStackTrace();
//                    }
//                }
//            });
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
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        System.out.println("Get New Client");
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
            System.out.println(ex.getMessage());
        }
        socketChannel.writeAndFlush("hello from TestServerHandle\n");
    }

    static public class TestServerHandle extends SimpleChannelInboundHandler<String>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception
        {
            System.out.println("TestServerHandle handle message: "+s);
            channelHandlerContext.writeAndFlush("wirte test server");
        }
    }

    static public class TestHttpServerHandle extends SimpleChannelInboundHandler<HttpRequest>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) throws Exception
        {
            System.out.println("TestHttpServerHandle handle message: "+httpRequest.uri()+" "+httpRequest.method().name()+" "+httpRequest.headers().size());

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("I am ok"
                    .getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            channelHandlerContext.write(response);
            channelHandlerContext.flush();
        }
    }
}
