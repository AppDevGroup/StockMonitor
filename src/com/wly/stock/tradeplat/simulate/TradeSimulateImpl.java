package com.wly.stock.tradeplat.simulate;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.stock.tradeplat.ITradeInterface;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockAsset;
import com.wly.stock.common.TradeBook;
import com.wly.user.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by wuly on 2017/2/13.
 */
public class TradeSimulateImpl implements ITradeInterface
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
        try {
            orderInfo.platOrderId = GetOrderId();

            final String UpdateFormat = "insert into trade_book(user_id, plat_id, plat_order_id, code, trade_flag, " +
                    "price, count, counter_fee, transfer_fee, stamp_tax, time, stat) " +
                    "values(%d, '%s', '%s', %d, %.2f, %d, %.2f, %.2f, %.2f, '%s', %d)";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(UpdateFormat, userInfo.id, userInfo.platId, orderInfo.platOrderId,
                    orderInfo.code,  orderInfo.tradeFlag, orderInfo.orderPrice, orderInfo.count, 0, 0, 0, Utils.GetDate(), orderInfo.GetStat()));
            orderInfo.id = Utils.GetLastInserId();
//            orderInfo.platOrderId = Utils.GetId();
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }

        orderInfo.platOrderId = GetOrderId();
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

    private String GetOrderId()
    {
        Random random = new Random();
        return String.format("%02d%s%06d%04d", 0, Utils.GetDateTime(), Utils.GetId(),  random.nextInt(10000));
    }
}
