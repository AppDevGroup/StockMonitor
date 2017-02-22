package com.wly.database;

import com.wly.common.LogUtils;
import com.wly.common.Utils;

import javax.management.Query;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * Created by wuly on 2016/11/25.
 */
public class DBPool implements Runnable
{
    static private DBPool s_instance = null;
    static  public DBPool GetInstance()
    {
        if(s_instance == null)
        {
            s_instance = new DBPool();
        }
        return  s_instance;
    }

    private  DBPool()  { }

    private LinkedList<DBQuery> taskQueue = new LinkedList<DBQuery>();
    private LinkedList<DBQuery> resultQueue = new LinkedList<DBQuery>();
    private LinkedList<String> taskQueueExecute = new LinkedList<String>();
    private DBOperator dbOper;

    public void Init(String jdbcUrl, String acct, String psw)
    {
        dbOper = new DBOperator();
        dbOper.Connect(jdbcUrl, acct, psw);

        Thread thread = new Thread(this);
        thread.start();
    }

    public DBQuery ExecuteQuerySync(String queryStr)
    {
        return dbOper.Query(queryStr);
    }

    public  void ExecuteQueryAsync(DBQuery query)
    {
        taskQueue.offer(query);
    }

    public  int ExecuteNoQuerySqlSync(String sqlStr, boolean getLastId)
    {
        return dbOper.Execute(sqlStr, getLastId);
    }

    public int ExecuteNoQuerySqlSync(String sqlStr)
    {
        return ExecuteNoQuerySqlSync(sqlStr, false);
    }

    public  void ExecuteNoQuerySqlAsync(String sqlStr)
    {
        taskQueueExecute.offer(sqlStr);
    }

    @Override
    public void run() {
        DBQuery query;
        String executeStr;
        int ret;
        while (true)
        {
            while ((query = taskQueue.poll()) != null) {
                dbOper.Query(query);
                dbOper.Close();
                resultQueue.offer(query);
            }

            while ((query = taskQueue.poll()) != null) {
                if (query.querySrc != null) {
                    query.querySrc.OnQueryResult(query);
                }
                query.Close();
            }

            while ((executeStr = taskQueueExecute.poll()) != null) {
                ret = dbOper.Execute(executeStr);
                if (ret == 0) {
                    LogUtils.GetLogger(LogUtils.LOG_DB).error("dbOper Execute failed! " + executeStr);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                LogUtils.GetLogger(LogUtils.LOG_DB).error(ex.getMessage());
            }
        }
    }
}
