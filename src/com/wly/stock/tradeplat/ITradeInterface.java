package com.wly.stock.tradeplat;

import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.TradeBook;
import com.wly.user.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */
public interface ITradeInterface
{
    void SetUserInfo(UserInfo userInfo);
    void Login(String acct, String psw); //登录帐号
    float GetRmbAsset();                                    //获取可用资金
    void DoOrder(OrderInfo orderInfo);                      //申请挂单
    void RevokeOrder(OrderInfo orderInfo);                  //撤销挂单
  //  void UpdateOrderStatus(ArrayList<OrderInfo> orderInfos);          //检查订单成交状态
    int GetOrderStatus(String platOrderId);            //检查订单状态
    List<TradeBook> GetTradeHis();      //获取订单
    int GetStockAssetCount(String code);            //获取可用数量
    float CacuTradeFee(int tradeFlag, String code, float price, int count); //计算交易手续费
}
