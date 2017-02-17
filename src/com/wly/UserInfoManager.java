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
    public static final int UserStat_Normal = 0;
    public static final int UserStat_Stop = 0;

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
    public ArrayList<Integer> platList = null;

    public boolean Init(ArrayList<Integer> platList)
    {
        this.platList = platList;
        GetUserInfo();
        FillUserPolicy();
        return true;
    }

    public boolean GetUserInfo()
    {
        try {
            String queryStr = String.format("select * from userinfo where stat = %d ", UserStat_Normal);
            if(platList != null && platList.size() > 0)
            {
                queryStr += " and plat_id in(";
                int i;
                for(i=0; i<platList.size(); ++i)
                {
                    if(i!=0)
                    {
                        queryStr+=", ";
                    }
                    queryStr += platList.get(i);
                }
                queryStr += ")";
            }

            UserInfo userInfo;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(queryStr);
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
