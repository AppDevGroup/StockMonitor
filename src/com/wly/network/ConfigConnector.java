package com.wly.network;

import org.dom4j.Element;

/**
 * Created by Administrator on 2016/12/5.
 */
public class ConfigConnector
{
    public int id;
    public String name;
    public String adress;
    public int port;

    static public ConfigConnector GetConfigByXmlElement(Element element)
    {
        ConfigConnector configConnector = new ConfigConnector();
        configConnector.id = Integer.parseInt(element.attributeValue("id"));
        configConnector.name = element.attributeValue("name");
        configConnector.adress = element.attributeValue("adress");
        configConnector.port = Integer.parseInt(element.attributeValue("port"));

        return configConnector;
    }

}
