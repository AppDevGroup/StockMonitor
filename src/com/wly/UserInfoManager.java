package com.wly;

import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockUtils;
import com.wly.stock.policy.PolicyStep;
import com.wly.user.UserInfo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/13.
 */
public class UserInfoManager
{
    static private UserInfoManager s_instance;
    static public UserInfoManager GetInstance()
    {
        if(s_instance == null)
        {
            s_instance = new UserInfoManager();
        }

        return s_instance;
    }

    public HashMap<Integer, UserInfo> userInfoHashMap = new HashMap<>();

    public boolean Init()
    {
        GetUserInfo();
        FillUserPolicy();
        return true;
    }

    public boolean GetUserInfo()
    {
        try {
            UserInfo userInfo;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync("select * from userinfo");
            ResultSet rs = dbQuery.resultSet;
            while (rs.next())
            {
                userInfo = new UserInfo();
                userInfo.id = rs.getInt("id");
                userInfo.platId = rs.getInt("plat_id");
                userInfo.platAcct = rs.getString("plat_acct");
                userInfo.platPsw = rs.getString("plat_psw");
                userInfoHashMap.put(userInfo.id, userInfo);
            }
            dbQuery.Close();
            return true;
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
            return  false;
        }
    }

    public void FillUserPolicy()
    {
        Iterator iter = userInfoHashMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            ((UserInfo)(entry.getValue())).Init();
        }
    }
}
