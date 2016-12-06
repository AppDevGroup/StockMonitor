package com.wly.network;

import org.dom4j.Element;

/**
 * Created by Administrator on 2016/12/5.
 */
public class ConfigAcceptor
{
    public int id;
    public String name;
    public int port;

    static public ConfigAcceptor GetConfigByXmlElement(Element element)
    {
        ConfigAcceptor configAcceptor = new ConfigAcceptor();
        configAcceptor.id = Integer.parseInt(element.attributeValue("id"));
        configAcceptor.name = element.attributeValue("name");
        configAcceptor.port = Integer.parseInt(element.attributeValue("port"));

        return configAcceptor;
    }
}
