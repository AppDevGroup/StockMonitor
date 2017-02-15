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
            dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitor", "root", "123456");

            PolicyStepAll policyStepAll;
            policyStepAll = new PolicyStepAll(null);
            policyStepAll.userId = 1;
            policyStepAll.code = "603858";
            policyStepAll.priceInit = 86.5f;
            policyStepAll.initCount = 600;
            policyStepAll.stepUnit = 100;
            policyStepAll.priceUnit = 1.5f;
            policyStepAll.minPrice = 75f;
            policyStepAll.maxPrice =  95f;
            policyStepAll.buyOffset = -0.09f;
            policyStepAll.sellOffset = -0.01f;
            policyStepAll.policyStat = PolicyStepAll.PolicyStat_Init;
            policyStepAll.buyOrderId = "0";
            policyStepAll.sellOrderId = "0";
            InsertPolicy(policyStepAll);
            System.out.println("insert finish!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static private  void InsertPolicy(PolicyStepAll policy)
    {
        try
        {
            final String SqlFomat = "insert into policy_step (user_id, code, price_init, count_init," +
                    "price_unit, step_unit, buy_offset, sell_offset, min_price, max_price,  policy_stat," +
                    "price_last, buyorder_id, buyorder_date, sellorder_id, sellorder_date)" +
                    "values(%d, '%s', %.2f, %d, %.2f, %d, %.2f, %.2f, %.2f, %.2f, %d, %.2f, '%s', '%s', '%s', '%s')";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(SqlFomat, policy.userId, policy.code, policy.priceInit, policy.initCount,
                    policy.priceUnit, policy.stepUnit, policy.buyOffset, policy.sellOffset, policy.minPrice, policy.maxPrice, policy.policyStat,
                    policy.priceLast, policy.buyOrderId, "0", policy.sellOrderId, "0"));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
