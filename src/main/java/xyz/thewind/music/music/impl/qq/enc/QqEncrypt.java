package xyz.thewind.music.music.impl.qq.enc;

import xyz.thewind.music.util.Md5Encrypt;

import java.util.Random;

public class QqEncrypt {
    private static final String encNonce="CJBPACrRuNy7";
    private static final String signPrxfix="zza";
    private static final char[] dir="0234567890abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     *
     *@param encParams 需要加密的参数，这是一段请求体数据，为json字符串格式，例如下面的格式，可以抓包获取
     *                 {"comm":{"ct":24,"cv":0},"vip":{"module":"userInfo…baseinfo_v2","param":{"vec_uin":["3011429848"]}}}
     *
     * @return 加密的方式为固定字串 zza加上一个10-16位的随机字符串再加上 固定字串 CJBPACrRuNy7加上请求数据拼接的 MD5值
     */
    public static String getSign(String encParams){
        return signPrxfix+uuidGenerate()+ Md5Encrypt.convertToMd5(encNonce+encParams);
    }

    private static String uuidGenerate(){
        int minLen=10;
        int maxLen=16;
        Random ran=new Random(System.currentTimeMillis());
        int ranLen=ran.nextInt(maxLen-minLen)+minLen;
        StringBuilder sb=new StringBuilder(ranLen);
        for (int i=0;i<ranLen;i++){
            sb.append(dir[ran.nextInt(dir.length)]);
        }
        return sb.toString();
    }
}
