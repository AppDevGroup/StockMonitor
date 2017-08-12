package com.wly;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.util.Scanner;

/**
 * Created by wuly on 2017/4/5.
 */
public class TestVCode
{
    private static final String datapath = ".";
    private static final String testResourcesDataPath = "test/resources/test-data";
    private static final String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
    private static final String expOCRResult1 = "0123456789";

    static public void main(String[] args)
    {
        ITesseract instance;
        instance = new Tesseract();
//        instance.setDatapath(new File(datapath).getPath());

        Scanner in=new Scanner(System.in);
        System.out.println("needinput:");
        System.out.println(in.nextLine());

        try
        {
            File imageFile = new File(".", "test.png");
            String expResult = expOCRResult1;
            String result = instance.doOCR(imageFile);
            System.out.print("result: "+result);
        }
        catch (Exception ex)
        {
            System.out.print(ex.getMessage());
        }
    }
}
