package com.wly.stock;

/**
 * Created by wuly on 2017/6/11.
 */
public class StockAnalysis {
    public static void main(String[] args)
    {
        float prefitLastYear = 0.6f; //2834
        float prefitLastYearGrow = 1.4390f;
        float prefitLastQuarterGrow = 0.2473f;
        float standardPE = 0.04f;
        float split = 1f;

        float priceLastYearStandrad = (prefitLastYear/standardPE)/split;
        float priceLastQuarterStandrad = priceLastYearStandrad+priceLastYearStandrad*prefitLastQuarterGrow;
        System.out.println(String.format("priceLastYearStandrad: %.2f \npriceLastQuarterStandrad %.2f", priceLastYearStandrad, priceLastQuarterStandrad));

        System.out.println("20170611");
        System.out.println(String.format("%.2f %.2f%%", priceLastYearStandrad, prefitLastYearGrow*100));
        System.out.println(String.format("    %.2f %.2f%%", priceLastQuarterStandrad, prefitLastQuarterGrow*100));
    }
}
