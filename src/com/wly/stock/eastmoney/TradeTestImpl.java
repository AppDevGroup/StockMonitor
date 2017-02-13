package com.wly.stock.eastmoney;

import com.wly.common.Utils;
import com.wly.stock.StockConst;
import com.wly.stock.common.ITradeInterface;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockAsset;
import com.wly.stock.common.TradeBook;
import com.wly.user.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuly on 2017/2/13.
 */
public class TradeTestImpl implements ITradeInterface
{
    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();

    private UserInfo userInfo;
    @Override
    public void SetUserInfo(UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    @Override
    public void Login(String acct, String psw)
    {
    }

    @Override
    public boolean UpdateUserAsset()
    {
        userInfo.rmbAsset.activeAmount = 999999999;
        return false;
    }

    @Override
    public void DoOrder(OrderInfo orderInfo)
    {
        orderInfo.platOrderId = Integer.toString(Utils.GetId());
        orderInfos.add(orderInfo);
    }

    @Override
    public void RevokeOrder(OrderInfo orderInfo)
    {
        orderInfo.SetStat(OrderInfo.OderStat_Cancel);
    }

    @Override
    public void UpdateOrderStatus(ArrayList<OrderInfo> orderInfos)
    {
        int i;
        OrderInfo orderInfo;
        for(i=0; i<orderInfos.size(); ++i)
        {
            orderInfo = orderInfos.get(i);
            if(orderInfo.GetStat() == OrderInfo.OderStat_Order)
            {
                orderInfo.SetStat(OrderInfo.OderStat_Deal);
            }
        }
    }

    @Override
    public List<TradeBook> GetTradeHis()
    {
        return null;
    }

    @Override
    public List<StockAsset> GetAssetList()
    {
        return null;
    }

    @Override
    public float CacuTradeFee(int tradeFlag, String code, float price, int count)
    {
        return 0;
    }
}
