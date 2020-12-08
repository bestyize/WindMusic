package xyz.thewind.music.util;

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
