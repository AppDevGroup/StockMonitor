import com.wly.common.Utils;
import com.wly.stock.StockInfoProviderSina;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/22.
 */
public class MainClass {
    static  public  void main(String[] agrs) throws Exception
    {
        Timer timer = new Timer();
        timer.schedule(new TaskQueryStock(), 0, 1000 );
    }
}

class TaskQueryStock extends TimerTask
{
    @Override
    public void run()
    {
        try {
            StockInfoProviderSina provider = new StockInfoProviderSina();
            ArrayList<Integer> codeList = new ArrayList<Integer>();
            codeList.add(603020);
//            codeList.add(603309);
            Timer timer = new Timer();
            provider.GetStockInfoByCode(codeList);
        }
        catch (Exception ex)
        {
            Utils.Log(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
