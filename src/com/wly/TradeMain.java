//package com.wly;
//
//import com.wly.common.Utils;
//import com.wly.database.DBPool;
//import com.wly.stock.analysis.TotalTatistics;
//import com.wly.stock.analysis.TradeInfoManager;
//
///**
// * Created by wuly on 2016/11/26.
// */
//public class TradeMain
//{
//    static public void main(String[] args)
//    {
//        Init();
//
//        TotalTatistics tactic = new TotalTatistics();
//        tactic.SimpleTatistic(TradeInfoManager.GetInstance().tradeList, TradeInfoManager.GetInstance().myAsset);
//    }
//
//    static  private void Init()
//    {
//        try
//        {
//            DBPool dbPool = DBPool.GetInstance();
//            dbPool.Init("jdbc:mysql://sql6.freesqldatabase.com/sql6145865", "sql6145865", "Rj4ABJv2H9");
//            TradeInfoManager.GetInstance().Init();
//        }
//        catch (Exception ex)
//        {
//            Utils.LogException(ex);
//        }
//    }
//}
