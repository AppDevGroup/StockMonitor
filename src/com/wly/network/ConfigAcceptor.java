package com.wly.network;

import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class ConfigAcceptor
{
    public int id;
    public String name;
    public int port;
    public ArrayList<String> handleList = new ArrayList<>();

    static public ConfigAcceptor GetConfigByXmlElement(Element element)
    {
        ConfigAcceptor configAcceptor = new ConfigAcceptor();
        configAcceptor.id = Integer.parseInt(element.attributeValue("id"));
        configAcceptor.name = element.attributeValue("name");
        configAcceptor.port = Integer.parseInt(element.attributeValue("port"));

        List<Node>  nodeList = element.selectNodes("handle");
        for(Node node:nodeList)
        {
            configAcceptor.handleList.add(((Element)node).attributeValue("class"));
        }

        return configAcceptor;
    }
}
