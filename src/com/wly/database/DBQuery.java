package com.wly.database;

import com.wly.common.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wuly on 2016/11/25.
 */
public class DBQuery
{
    public int id;
    public String queryStr;
    public IDBQuery querySrc;
    public ResultSet resultSet;
    public Connection con = null;
    public Statement stmt = null;

    public void Close()
    {
        if(resultSet != null)
        {
            try
            {
                resultSet.close();
                resultSet = null;
            }
            catch (Exception ex)
            {
                Utils.LogException(ex);
            }
        }

        if(stmt != null)
        {
            try
            {
                stmt.close();
                stmt = null;
            }
            catch (Exception ex)
            {
                Utils.LogException(ex);
            }
        }

        if(con != null)
        {
            try
            {
                con.close();
                con = null;
            }
            catch (Exception ex)
            {
                Utils.LogException(ex);
            }
        }
    }
}
