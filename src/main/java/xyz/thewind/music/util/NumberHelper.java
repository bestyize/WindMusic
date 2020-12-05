package xyz.thewind.music.util;

import org.springframework.data.relational.core.sql.In;

public class NumberHelper {
    public static int parseNumber(String num,int defaultVal){
        int realNum=defaultVal;
        try {
            realNum=Integer.parseInt(num);
        }catch (Exception e){
            e.printStackTrace();
        }
        return realNum;
    }
}
