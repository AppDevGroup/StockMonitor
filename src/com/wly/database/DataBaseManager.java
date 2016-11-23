package com.wly.database;

import com.mchange.v2.c3p0.DataSources;
import com.mysql.jdbc.Util;
import com.wly.common.Utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Administrator on 2016/11/23.
 */
public class DataBaseManager
{
    static private DataBaseManager s_instance = null;
    static  public DataBaseManager GetInstance()
    {
        if(s_instance == null)
        {
            s_instance = new DataBaseManager();
        }
        return  s_instance;
    }

    private  DataBaseManager()
    {
    }

    private String jdbcUrl;
    private String acct;
    private String psw;
    private DataSource ds;

    public  void Init(String jdbcUrl, String acct, String psw)
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
            Utils.Log(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public ResultSet ExecuteQuery(String sqlstr)
    {
        ResultSet rs = null;
        try
        {
            Connection con = ds.getConnection();
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sqlstr);
        }
        catch (Exception ex)
        {
            Utils.Log(ex.getMessage());
            ex.printStackTrace();
        }

        return rs;
    }

    public  int ExecuteUpdate(String sqlstr)
    {
        int ret = 0;
        try
        {
            Connection con = ds.getConnection();
            Statement stmt = con.createStatement();
            ret = stmt.executeUpdate(sqlstr);
        }
        catch (Exception ex)
        {
            Utils.Log(ex.getMessage());
            ex.printStackTrace();
        }

        return ret;
    }
}
