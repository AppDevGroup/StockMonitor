package com.wly.stock.common;

/**
 * Created by Administrator on 2017/2/25.
 */
public class StockBaseInfo
{
    public String code;
    public String name;
    public float price;
    public float pe;          //市盈
    public float mktcao;    //总市值    单位:万元
    public float nmc;       //流通市值  单位:万元
    public long totalCount; //总股本    单位:万元
    public long activeCount; //流通股本
}
