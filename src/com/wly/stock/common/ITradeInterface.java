package com.wly.stock.common;

import com.wly.user.UserInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */
public interface ITradeInterface
{
    void Login(String acct, String psw); //登录帐号
    boolean FillUserAsset(UserInfo userInfo);                //获取可用资金
    void DoOrder(OrderInfo orderInfo);  //申请挂单
    boolean CheckOrderState(OrderInfo orderInfo);          //检查订单成交状态
    List<TradeBook> GetTradeHis();      //获取订单
    List<StockAsset> GetAssetList();    //获取持仓列表
}
