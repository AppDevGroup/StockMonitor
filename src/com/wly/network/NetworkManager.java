package com.wly.network;

import com.wly.NettyServerTest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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

    private ArrayList<ConfigConnector> connectorConfList = new ArrayList<>();
    private ArrayList<ConfigAcceptor> acceptorConfList = new ArrayList<>();

    public void Init(String config)
    {
        try {
            SAXReader saxReader = new SAXReader();
            Document xmlDoc = saxReader.read(config);
            List<Node> nodeList;

            nodeList = xmlDoc.selectNodes("/network/acceptor/item");
            for (Node node : nodeList) {
                acceptorConfList.add(ConfigAcceptor.GetConfigByXmlElement((Element)node));
                StartAcceptor(ConfigAcceptor.GetConfigByXmlElement((Element)node));
            }

            nodeList = xmlDoc.selectNodes("/network/connector/item");
            for (Node node : nodeList) {
                connectorConfList.add(ConfigConnector.GetConfigByXmlElement((Element)node));
                StartConnector(ConfigConnector.GetConfigByXmlElement((Element)node));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void StartAcceptor(ConfigAcceptor conf)
    {
        Acceptor accpt = new Acceptor(conf);
        accpt.Start();
    }

    public  void StartConnector(ConfigConnector conf)
    {
        Connector connector = new Connector(conf);
        connector.Start();
    }
}
