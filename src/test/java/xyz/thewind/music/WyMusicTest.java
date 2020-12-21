package xyz.thewind.music;


import com.sun.jndi.toolkit.url.Uri;
import org.junit.Test;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static xyz.thewind.music.util.SongUtil.*;
import static xyz.thewind.music.util.SongUtil.QUALITY_LQ;

public class WyMusicTest {
    @Test
    public void testApi(){
        String params="A7CD7F5A002D2CD0337D11D62544C82A614694F7CB081EC4E6221FFA1FDCB423323165BC90E09A41083D99DC30DDFD04F129845ED5AAB8BED1920E5B6DD9459E050F99D22B0E6F9FD988D4E7290903E28A76601D9B52570DB1B4BA711AE3B97419AA6121415240A3A954C00A985432F9B85FAA0F1ABDB7FF5ABFB1C5C18870233476144A3A17D5304254C5BB8A20D67F54D3AA6177AAE64BD565DE5E19AC98EA9427A28FB57D574BE73966CEB08954675A8D035A18FBE667D5699D8960CD6884C3E1EFE385231901F0BA64F4612D6621FED2140072060D83F1E3419A3F63CA8D703C1EA066B0CB761E966E29BA6F131286F9E63C4CC1BB2CBD6D9EBF5548240219E6E1A5384144828B0D9FBF3ECEF9EF683C1F6FE12F9A54A80CB072CF72DA20814CD3F279EE8E01B6F0FC238E824AF491F6889E834F800A70F8EA43D7C375EDE059A5D185A0DBDA9573A9463DEFB2E21592EFDBED2FA4BB612DD34C3BE69C1C8669C1DCE2A45A7DACAFA0C320D13BB242AE2F543CE5959D5470A67A05C70D47152F32E6041176216F221644F252FAB3CCE9DBBA87ED1C5C0D1AAA54CF40D28D7BA7FE593989AAB5B46960CD06E9BDFBC7F002EBB81700CAA0C378C87815767D71D679E9C3D1ED5465A6CBD16F6D0B87FE9771B839D0A5A091641F576D82163C2E5E4F2983FE74A04E3331EFFC113B3AA9684B0DA6D044DA5397FAF7E8519D4B16D6F014AF5983FDF074C05D70A5958BC1F04A2E91ECA20326BB8E42B61C9A92427DC542BEC006B980F910EDE22CA1741BEFCD8EB1D704D1F403E035B987E7F7FCF0629C5D6A16E8F85C3A5AE14D082798B42E3D432A27A9B4365260FBAF2DF7286C1641FBFAA1CB1C1E71FCA178BF33917960C8FAB305BD05DF4DEBAB172805458206BA2A1054ED94287C2818A5D21919D0891DF55882054471F08DDB5B575ACD1628CFE98BFA9C5B25C91A123E2329DAAC823FE745E65DBB898D625BAEF1A45C6CB774348F50A33EA54CBC6C525A5D650B4CEF76E3154FB221719DA771E91C2A03C3240732CA88B862D579194DEEBF189FD0DBA7981EF26C6F095F16A332861677CDD23466574E53CFD19907666C675F6011F6FE56C865D1750A42BC7B2C9688921A40C5AE9F8D0A681225DC1C576F";
        System.out.println(WyEncrypt.eapiDecrypt(params));
    }
    @Test
    public void testSearch(){
//        String response=RequestModel.searchMusic("陈奕迅",0,1);
//        System.out.println(response);
//
//        String downloadLink=RequestModel.getMusicUrl("287063","SQ");
//        System.out.println(downloadLink);
//
//        String lyric=RequestModel.getLyric("287063");
//        System.out.println(lyric);
//        String comment=RequestModel.getComment("287063",0,20);
//        System.out.println(comment);
//        String mv=RequestModel.getMvUrl("5327432","SQ");
//        System.out.println(mv);
//        System.out.println(RequestModel.getMvDetail("5327432"));
        String user=RequestModel.login("17865161655","zr2014520");
        System.out.println(user);
//        System.out.println(RequestModel.getCategoryList("全部歌单","hot",0,10));
//        System.out.println(RequestModel.getCategoryDetail("5384248621",10));
//        System.out.println(RequestModel.getTopList());
//        System.out.println(RequestModel.getPlayUrl("287063","SQ"));
    }
    public static class RequestModel{
        private static final Map<String,String> qualityMap=new HashMap<>();
        private static final Map<String,String> headerMap=new HashMap<>();
        private static final Map<String,String> mvQualityMap=new HashMap<>();
        static {
            qualityMap.put(QUALITY_SQ,"999000");
            qualityMap.put(QUALITY_HQ,"320000");
            qualityMap.put(QUALITY_PQ,"192000");
            qualityMap.put(QUALITY_LQ,"128000");
            headerMap.put("Content-Type","application/x-www-form-urlencoded");
            headerMap.put("User-Agent","NeteaseMusic/8.0.20.1608209367(8000020);Dalvik/2.1.0 (Linux; U; Android 5.1.1; LIO-AN00 Build/LIO-AN00)");
            headerMap.put("Accept-Encoding","application/json");
            //headerMap.put("Cookie","MUSIC_U=ba0a04e302da6a12399d1817fe4eae9d3c505bac78f1c7412ddb705a2ca714c233a649814e309366");

            mvQualityMap.put(QUALITY_SQ,"1080");
            mvQualityMap.put(QUALITY_HQ,"720");
            mvQualityMap.put(QUALITY_PQ,"480");
            mvQualityMap.put(QUALITY_LQ,"240");

        }
        private static String[] apiServer={"https://interface.music.163.com","https://music.163.com"};
        public static String searchMusic(String keyword,int offset,int num){
            String queryParams="{\"s\":\""+keyword+"\",\"offset\":"+offset+",\"limit\":"+num+",\"header\":\"{}\",\"e_r\":\"false\",\"scene\":\"normal\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v1/search/song/get";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        public static String getPlayUrl(String songId,String quality){
            String queryParams="{\"ids\":\"[\\\""+songId+"\\\"]\",\"br\":\""+qualityMap.get(quality)+"\",\"e_r\": false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/song/enhance/player/url";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        public static String getDownloadUrl(String songId,String quality){
            String queryParams="{\"id\":\""+songId+"\",\"br\":\""+qualityMap.get(quality)+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/song/enhance/download/url";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }
        //{"id":"287063","lv":"-1","kv":"-1","tv":"-1","cp":"true","e_r":false,"header":"{}"}

        public static String getLyric(String songId){
            String queryParams="{\"id\":\""+songId+"\",\"lv\":\"-1\",\"kv\":\"-1\",\"tv\":\"-1\",\"cp\":\"true\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/song/lyric";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }
        //{"rid":"R_SO_4_287063","composeConcert":"true","offset":"0","total":"false","limit":"20","e_r":false,"header":"{}"}

        public static String getComment(String songId,int offset,int num){
            String queryParams="{\"rid\":\"R_SO_4_"+songId+"\",\"composeConcert\":\"true\",\"offset\":\""+offset+"\",\"total\":\"false\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v1/resource/comments/R_SO_4_"+songId;
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }
        //https://interface.music.163.com/eapi/song/enhance/play/mv/url
        public static String getMvUrl(String mvId,String quality){
            String queryParams="{\"id\":\""+mvId+"\",\"r\":\""+mvQualityMap.get(quality)+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/song/enhance/play/mv/url";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        public static String getMvDetail(String mvId){
            String queryParams="{\"id\":\""+mvId+"\",\"type\":\"mp4\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v1/mv/detail";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        public static String getMvList(String area,int offset,int num){
            String queryParams="{\"area\":\""+area+"\",\"offset\":\""+offset+"\",\"total\":\"false\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/mv/toplist";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        //{"password":"b3ae63bff4270d3a1e1dbeea408e608a","type":"1","remember":"true","https":"true","checkToken":"9ca17ae2e6ffcda170e2e6eeb7e270acf1babaef609b9e8eb6d85f968f9f84f86a81afbca3f96d8f8bfba4c52af0feaec3b92ae9b8b894c145a289a6b8dc4e928f8fb2c54e9baeaba5b57e95ad828ded219486ee9e","phone":"17865161655","countrycode":"86","e_r":false,"header":"{}"}

        public static String login(String phoneNumber,String password){
            String queryParams="{\"password\":\""+Md5Encrypt.convertToMd5(password)+"\",\"type\":\"1\",\"remember\":\"true\",\"https\":\"true\",\"checkToken\":\"9ca17ae2e6ffcda170e2e6eeb7e270acf1babaef609b9e8eb6d85f968f9f84f86a81afbca3f96d8f8bfba4c52af0feaec3b92ae9b8b894c145a289a6b8dc4e928f8fb2c54e9baeaba5b57e95ad828ded219486ee9e\",\"phone\":\""+phoneNumber+"\",\"countrycode\":\"86\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/w/login/cellphone";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         *
         * @param cat 全部歌单、流行、摇滚、民谣、电子、另类/独立、轻音乐、综艺、影视原声、ACG
         * @param type hot
         * @param offset 0
         * @param num 10
         * @return
         */
        public static String getCategoryList(String cat,String type,int offset,int num){
            String queryParams="{\"cat\":\""+cat+"\",\"type\":\""+type+"\",\"offset\":\""+offset+"\",\"total\":\"true\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/playlist/category/list";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         *
         * @param cat 全部歌单、华语、欧美、韩语、日语、粤语、流行、摇滚、民谣、电子、另类/独立、轻音乐、综艺、影视原声、ACG
         * @param type hot
         * @param offset 0
         * @param num 10
         * @return
         */
        public static String getHighQualityCategoryList(String cat,String type,int offset,int num){
            String queryParams="{\"cat\":\""+cat+"\",\"type\":\""+type+"\",\"offset\":\""+offset+"\",\"total\":\"true\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/playlist/highquality/list";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         * 歌单详情
         * /eapi/v6/playlist/detail /eapi/playlist/detail /eapi/playlist/v4/detail
         * @param listId
         * @param num
         * @return
         */
        public static String getCategoryDetail(String listId,int num){
            String queryParams="{\"id\":\""+listId+"\",\"n\":\""+num+"\",\"s\":\"0\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v6/playlist/detail";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         * 获取排行榜
         * @return
         */
        public static String getTopList(){
            String queryParams="{\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/toplist";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         * 获取排行榜歌单内容
         * @param topListId
         * @param num
         * @return
         */
        public static String getTopListDetail(String topListId,int num){
            return getCategoryDetail(topListId,num);
        }

        /**
         *
         * @param type
         * @return
         */
        public static String getArtistListByType(int type){
            String queryParams="{\"type\":\""+type+"\",{\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/toplist/artist";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         *
         * @param area 地区
         * @param type 类型
         * @param offset 偏移
         * @param num 数量
         * @return
         */
        public static String getArtist(int area,int type,int offset,int num){
            String queryParams="{\"area\":\""+area+"\",\"type\":\""+type+"\",\"initial\":\"-1\",\"offset\":\""+offset+"\",\"total\":\"true\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
            String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v1/artist/list";
            String params=WyEncrypt.eapiEncrypt(api,queryParams);
            return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
        }

        /**
         * 获取歌手排行，无关地区
         * @param offset
         * @param num
         * @return
         */
        public static String getArtist(int offset,int num){
            return getArtist(-1,-1,offset,num);
        }

    }

    public static class WyEncrypt{
        public static final byte BYTE_MAX_VALUE = -1;
        public static String encKey="e82ckenh8dichen8";

        public static String eapiEncrypt(String url,String clearParams){
            try {
                Uri uri = new Uri(url);
                String path=uri.getPath().replaceFirst("/eapi/","/api/");
                String message="nobody"+path+"use"+clearParams+"md5forencrypt";
                String data=path+"-36cd479b6b5-"+clearParams+"-36cd479b6b5-"+ Md5Encrypt.convertToMd5(message);
                return WyEncrypt.encodeCipherText(WyEncrypt.encrypt(data,encKey));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        public static String eapiDecrypt(String cipherText){
            return new String(WyEncrypt.decrypt(WyEncrypt.decodeCipherText(cipherText),WyEncrypt.encKey));
        }

        private static byte[] encrypt(String text,String key){
            return encrypt(text,key,null,"AES/ECB/PKCS5Padding");
        }

        private static byte[] decrypt(byte[]cipherText,String key){
            return decrypt(cipherText,key,null,"AES/ECB/PKCS5Padding");
        }

        private static byte[] encrypt(String text,String key,String iv,String encMode){
            try{
                SecretKeySpec spec=new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"AES");
                Cipher cipher=Cipher.getInstance(encMode);
                if(iv==null){
                    cipher.init(Cipher.ENCRYPT_MODE,spec);
                }else {
                    cipher.init(Cipher.ENCRYPT_MODE,spec,new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                }
                return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }


        private static byte[] decrypt(byte[] cipherText,String key,String iv,String encMode){
            try{
                SecretKeySpec spec=new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"AES");
                Cipher cipher=Cipher.getInstance(encMode);
                if(iv==null){
                    cipher.init(Cipher.DECRYPT_MODE,spec);
                }else {
                    cipher.init(Cipher.DECRYPT_MODE,spec,new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                }
                return cipher.doFinal(cipherText);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        private static String encodeCipherText(byte[] bArr) {
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b2 : bArr) {
                String hexString = Integer.toHexString((b2 & BYTE_MAX_VALUE));
                if (hexString.length() == 1) {
                    hexString = '0' + hexString;
                }

                hexString=hexString.substring(hexString.length()-2);

                stringBuffer.append(hexString.toUpperCase());
            }
            return stringBuffer.toString();
        }

        private static byte[] decodeCipherText(String str) {
            if (str.length() < 1) {
                return null;
            }
            byte[] bArr = new byte[(str.length() / 2)];
            for (int i2 = 0; i2 < str.length() / 2; i2++) {
                int i3 = i2 * 2;
                int i4 = i3 + 1;
                bArr[i2] = (byte) ((Integer.parseInt(str.substring(i3, i4), 16) * 16) + Integer.parseInt(str.substring(i4, i3 + 2), 16));
            }
            return bArr;
        }

    }
}
