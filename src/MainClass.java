import com.mysql.jdbc.Util;
import com.wly.common.Utils;
import com.wly.database.DataBaseManager;
import com.wly.stock.StockInfo;
import com.wly.stock.StockInfoProviderSina;
import com.wly.stock.StockUtils;
import com.wly.stock.policy.PolicyStep;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/22.
 */
public class MainClass {
    static  public  void main(String[] agrs) throws Exception
    {
        Init();
        Timer timer = new Timer();
        timer.schedule(new TaskQueryStock(), 0, 2000);
        //InsertPolicy();
        //StockUtils.DoTradeSell(1, 603020, 22.18f, 300);
    }

    static  private void Init()
    {
        try
        {
            DataBaseManager dbMgr = DataBaseManager.GetInstance();
            dbMgr.Init("jdbc:mysql://sql6.freesqldatabase.com/sql6145865", "sql6145865", "Rj4ABJv2H9");
            PolicyStep.Init();
        }
        catch (Exception ex)
        {
                Utils.LogException(ex);
        }
    }

    static private void InsertPolicy()
    {
        DataBaseManager dbMgr = DataBaseManager.GetInstance();
        dbMgr.ExecuteUpdate("insert into policy_step (code, price_init, price_unit, price_last, step_unit)" +
                "values('603515', 46.50, 0.50, 46.50, 200)");
    }

    static  public  void PrccessStockInfo(ArrayList<StockInfo> ArrayList)
    {
        int i;
        PolicyStep policyStep;
        int len = ArrayList.size();
        for(i=0;i<ArrayList.size(); ++i)
        {
            if(PolicyStep.PolicyStepHashMap.containsKey(ArrayList.get(i).code))
            {
                policyStep = PolicyStep.PolicyStepHashMap.get(ArrayList.get(i).code);
                policyStep.PrcessPrice(ArrayList.get(i));
            }
        }
    }
}

class TaskQueryStock extends TimerTask
{
    @Override
    public void run()
    {
        try {
            StockInfoProviderSina provider = new StockInfoProviderSina();
            MainClass.PrccessStockInfo(provider.GetStockInfoByCode(StockUtils.QueryCodeList));
        }
        catch (Exception ex)
        {
            Utils.LogException(ex);
        }
    }
}
