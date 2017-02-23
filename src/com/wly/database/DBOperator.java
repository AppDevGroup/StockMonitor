package com.wly.database;

import com.mchange.v2.c3p0.DataSources;
import com.wly.common.LogUtils;
import com.wly.common.Utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wuly on 2016/11/25.
 */
public class DBOperator
{
    private String jdbcUrl;
    private String acct;
    private String psw;
    private DataSource ds;

    public boolean Connect(String jdbcUrl, String acct, String psw)
    {
        this.jdbcUrl = jdbcUrl;
        this.acct = acct;
        this.psw = psw;

        try
        {
            ds = DataSources.unpooledDataSource(jdbcUrl, acct, psw);
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_DB).error(ex.getMessage());
            ex.printStackTrace();
            return false;
        }

        return  true;
    }

    public DBQuery Query(String queryStr)
    {
        DBQuery dbQuery = new DBQuery();
        dbQuery.queryStr = queryStr;
        return Query(dbQuery);
    }

    public DBQuery Query(DBQuery dbQuery)
    {
        //LogUtils.LogDB(dbQuery.queryStr);
        try
        {
            dbQuery.con = ds.getConnection();
            dbQuery.stmt = dbQuery.con.createStatement();
            dbQuery.resultSet = dbQuery.stmt.executeQuery(dbQuery.queryStr);
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_DB).error(ex.getMessage());
            ex.printStackTrace();
        }

        return dbQuery;
    }

    public int Execute(String str)
    {
        return Execute(str, false);
    }

    public int Execute(String str, boolean getLastId)
    {
        LogUtils.LogDB(str);
        int ret = 0;
        try
        {
            Connection con = ds.getConnection();
            Statement stmt = con.createStatement();
            if(getLastId)
            {
                ret = stmt.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
            }
            else
            {
                ret = stmt.executeUpdate(str);
            }
            ResultSet rs;
            if (getLastId)
            {
                rs = stmt.getGeneratedKeys(); //获取结果
                if (rs.next())
                {
                    ret = rs.getInt(1);//取得ID
                }
                else
                {
                    // throw an exception from here
                    Utils.Log("Get Last id failed! "+str);
                }
            }

            stmt.close();
            con.close();

        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_DB).error(ex.getMessage());
            ex.printStackTrace();
        }

        return ret;
    }

    public  void Close()
    {
    }
}
