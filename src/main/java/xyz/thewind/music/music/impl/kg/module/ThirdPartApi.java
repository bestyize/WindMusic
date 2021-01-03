package xyz.thewind.music.music.impl.kg.module;

import xyz.thewind.music.util.HttpRequestHelper;

public class ThirdPartApi {
    public static String getPlayUrl(String songId,String quality){
        return YueTingDumper.getPlayUrl(songId);
    }

    private static class YueTingDumper{
        private static final String baseApi="http://139.9.188.222:8000/kugou/song?data=";
        public static String getPlayUrl(String songId){
            if(songId.length()!=32){
                return "";
            }
            String data="{\"hash\":\"" + songId + "\",\"album_id\":\"\",\"time\":" + System.currentTimeMillis() + "}";
            String response= HttpRequestHelper.downloadWebSiteUseGet(baseApi+encrypt(data),null);
            if(response.contains("http://")){
                response=response.substring(response.indexOf("http://"));
                response=response.substring(0,response.indexOf("\""));
            }else {
                response="";
            }
            return response;
        }

        public static String encrypt(String str) {
            if (str == null) {
                return "";
            }
            char[] alphaBaseReverseArr = "abcdefghijklmnopqrstvuwxyzSTRING".toCharArray();
            char[] cArr = "STRING".toCharArray();
            char[] charArray = str.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < charArray.length; i++) {
                int length = cArr[i % cArr.length] % cArr.length;
                char c = charArray[i];
                int length2 = (c / 26) + length;
                int length3 = (c % 26) + length;
                sb.append(String.valueOf(alphaBaseReverseArr[length2]) + alphaBaseReverseArr[length3]);
            }
            return sb.toString();
        }
    }
}
