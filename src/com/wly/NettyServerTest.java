package com.wly;

import com.wly.network.NetworkManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class NettyServerTest
{
    static public void main(String[] args)
    {
     //   TestXml();

        NetworkManager.GetInstance().Init("./config/network.xml");
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//
//        try {
//            ServerBootstrap serverBootstrap = new ServerBootstrap();
//            serverBootstrap.channel(NioServerSocketChannel.class);
//            serverBootstrap.group(bossGroup, workerGroup);
//            serverBootstrap.localAddress(new InetSocketAddress(3344));// 设置监听端口
//            serverBootstrap.childHandler(new ChannelInitHandle());
//            ChannelFuture cf = serverBootstrap.bind().sync();
//            System.out.println("listen on 3344");
//            PrintCurentThreadId();
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
    }

    static public class ChannelInitHandle extends ChannelInitializer<SocketChannel>
    {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            System.out.println("ChanleInitHandle initChannel");
            PrintCurentThreadId();
        }
    }

    static public void TestXml()
    {
        try {
            SAXReader saxReader = new SAXReader();
            Document xmlDoc = saxReader.read("./config/network.xml");
            List<Node> nodeList = xmlDoc.selectNodes("/network/acceptor/item");

            Element elementItem;
            for (Node node : nodeList)
            {
                elementItem = (Element)node;
                System.out.print("server conf: "+elementItem.attributeValue("port"));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    static public  void PrintCurentThreadId()
    {
        System.out.println("Thread Id: "+Thread.currentThread().getId());
    }
}