package com.wly.user;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.stock.StockConst;
import com.wly.stock.policy.PolicyStepAll;

/**
 * Created by Administrator on 2017/2/14.
 */
public class AddUserInfo
{
    public static void main(String[] args)
    {
        try
        {
            DBPool dbPool = DBPool.GetInstance();
            dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitor?useSSL=true", "root", "123456");
            InsertPolicy();
            //InsertUser();
            System.out.println("insert finish!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static private  void InsertPolicy()
    {
        try
        {
            PolicyStepAll policy;
            policy = new PolicyStepAll(null);
            policy.userId = 2;
            policy.code = "603116";
            policy.priceInit = 19.5f;
            policy.initCount = 2000;
            policy.stepUnit = 300;
            policy.priceUnit = 0.5f;
            policy.minPrice = 18f;
            policy.maxPrice =  26f;
            policy.buyOffset = -0.04f;
            policy.sellOffset = -0.01f;
            policy.policyStat = PolicyStepAll.PolicyStat_Init;
            policy.buyOrderId = "0";
            policy.sellOrderId = "0";

            final String SqlFormat = "insert into policy_step (user_id, code, price_init, count_init," +
                    "price_unit, step_unit, buy_offset, sell_offset, min_price, max_price,  policy_stat," +
                    "price_last, buyorder_id, buyorder_date, sellorder_id, sellorder_date)" +
                    "values(%d, '%s', %.2f, %d, %.2f, %d, %.2f, %.2f, %.2f, %.2f, %d, %.2f, '%s', '%s', '%s', '%s')";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(SqlFormat, policy.userId, policy.code, policy.priceInit, policy.initCount,
                    policy.priceUnit, policy.stepUnit, policy.buyOffset, policy.sellOffset, policy.minPrice, policy.maxPrice, policy.policyStat,
                    policy.priceLast, policy.buyOrderId, "0", policy.sellOrderId, "0"));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static private void InsertUser()
    {
        UserInfo userInfo = new UserInfo();
        userInfo.platId = 0;
        userInfo.platAcct = "121323";
        userInfo.platPsw = "121323";

        final String SqlFormat = "insert into userinfo(plat_id, plat_acct, plat_psw) " +
                "values(%d, '%s', '%s')";
        DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(SqlFormat, userInfo.platId, userInfo.platAcct, userInfo.platPsw));
    }
}
