package xyz.thewind.music.music.impl;


import org.junit.Test;
import xyz.thewind.music.music.impl.wy.enc.CryptoUtil;
import xyz.thewind.music.music.impl.wy.enc.WyEncrypt;
import xyz.thewind.music.music.impl.wy.module.RequestModule;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static xyz.thewind.music.music.impl.wy.module.RequestModule.apiServer;

public class WyMusicTest {
    @Test
    public void testApi(){
        String params="32115EE97DB38F730DBAE0AD7246F7E14CDC6C09D0E801EC1D096885562D4F402237C43D714133BBE34979DF5B1807444F8B3DC42FC70D62BACF64EEF37BA62D191A775A0547112B63283E72116D78D7A900A46A3C76486A2B638A780DB3F4B1B9B94044ED7E0FB350D2C76EA332C0F5041E8BBD216394CD331458FC20B725570AA4663AC447777301B9D60F3D6134F4236F605AA778BA59EF20C3ADEE8F200A3D37C04B0963FEC59355AB083BD7AEE7597B8BC6FDC72983912D40DF128D479EA25EC4F1525EB7ED73B16EAEA7E28DAE82E11C8E5DF43895036E2F072B9E94FD60E1B972DEBC3CD4D6668CECBF783D97C50E4F9075AA645AC05809B70668D19555205E6E931126CA0DE2EE72FF83FDE8AFBD88CCF6F576FBA52749E1BC9933D3827C686AC910B0BD86B9D71CBB0EC7123B2CCF49F4F4FBE5DC81656F287A4C2A51023727B9BB4E0F22367A8AC04BAB357D76C3C76D36695CD07539F117B3E063B5BA88CFEC7D779DE24581D2FE216CF75F4025D86DA6AD821DBF77D57B62085D4E7AB312BFC1F5F7E444BC181291A8E1A484D8F6C7A2B1E5BD130F7DFEDDB87AED58A46B15D6C16158D661B9B7F499DEE0BD77EB8CDE1E1F127FD867B51115DE6781037A110A01755B5DD487C2852C74050F99D22B0E6F9FD988D4E7290903E28A76601D9B52570DB1B4BA711AE3B97419AA6121415240A3A954C00A985432F9B85FAA0F1ABDB7FF5ABFB1C5C1887023C93C22ED394CA79260716A3FBF7EDC381B9348EBBC16C2A7266377E098D22B492D4D3187099EDE729DD748E7DC1CD338372225E18FD62D3B9E2B22A38BC8892C";
        System.out.println(WyEncrypt.eapiDecrypt(params));
    }
    @Test
    public void testSearch(){
//        System.out.println(RequestModule.searchSong("陈奕迅",0,1));
//        System.out.println(RequestModule.getPlayUrl("287063","SQ"));
//        System.out.println(RequestModule.getLyric("287063"));
//        System.out.println(RequestModule.getComment("287063",0,20));
//        System.out.println(RequestModule.getMvUrl("5327432","SQ"));
//        System.out.println(RequestModule.getMvDetail("5327432"));
//        System.out.println(RequestModule.login("17865161655","zr2014520"));
//        System.out.println(RequestModule.getCategoryList("全部歌单","hot",0,10));
//        System.out.println(RequestModule.getCategoryDetail("5384248621",10));
//        System.out.println(RequestModule.getTopList());
//        System.out.println(RequestModule.getPlayUrl("287063","SQ"));
//        System.out.println(RequestModule.getSongDetail("287063"));
//        System.out.println(RequestModule.getAlbumDetail("34780271"));
//        System.out.println(RequestModule.getSimPlayList("287063",0,20));
        System.out.println(RequestModule.getSimSongList("36990266",0,20));
    }
    @Test
    public void testRebuild(){
        rebuildRequest("FA90B329E9614F79E79598F37DC2EDB430F8378D2A2796338F0BFDEAEF824A22975CDA9D96D79E6DC4A59218CDB8199F24175ED04973A6406CBD9B1B063FA4C85E515A9009F6AEB8CB91A25582996176E059A5D185A0DBDA9573A9463DEFB2E21592EFDBED2FA4BB612DD34C3BE69C1C8669C1DCE2A45A7DACAFA0C320D13BB242AE2F543CE5959D5470A67A05C70D47152F32E6041176216F221644F252FAB3CCE9DBBA87ED1C5C0D1AAA54CF40D28D7BA7FE593989AAB5B46960CD06E9BDFBC7F002EBB81700CAA0C378C87815767D71D679E9C3D1ED5465A6CBD16F6D0B87D9C3F0B414FD2BAA1944B9270519F4702E5E4F2983FE74A04E3331EFFC113B3AA9684B0DA6D044DA5397FAF7E8519D4B16D6F014AF5983FDF074C05D70A5958BC1F04A2E91ECA20326BB8E42B61C9A92427DC542BEC006B980F910EDE22CA1741BEFCD8EB1D704D1F403E035B987E7F7FCF0629C5D6A16E8F85C3A5AE14D082798B42E3D432A27A9B4365260FBAF2DF7286C1641FBFAA1CB1C1E71FCA178BF33917960C8FAB305BD05DF4DEBAB172805458206BA2A1054ED94287C2818A5D21919D0891DF55882054471F08DDB5B575ACD1628CFE98BFA9C5B25C91A123E2329DAAC823FE745E65DBB898D625BAEF1A45C6CB774348F50A33EA54CBC6C525A5D650B4CEF76E3154FB221719DA771E91C2A03C3240732CA88B862D579194DEEBF189FD0DBA7981EF26C6F095F16A33286C8AE9E7F7E89A4C04B67DC4A38FD4ED6BF42AC5A0E69147859A7EA98BC94E00E40A32D6ACA11690BF2982BE145A30B0B");
    }

    public void rebuildRequest(String params){
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Content-Type","application/x-www-form-urlencoded");
        headerMap.put("User-Agent","NeteaseMusic/8.0.20.1608209367(8000020);Dalvik/2.1.0 (Linux; U; Android 5.1.1; LIO-AN00 Build/LIO-AN00)");
        headerMap.put("Accept-Encoding","application/json");
        headerMap.put("Cookie","Cookie: os=pc; deviceId=1D63C53F2580E40EBC0B0D2447028F4028DA671540A3CCFF4056; osver=Microsoft-Windows-10-Professional-build-19042-64bit; appver=2.7.5.198554; channel=netease; MUSIC_U=ba0a04e302da6a12399d1817fe4eae9dc2220e1622ff7944a9254603f698478a33a649814e309366; NMTID=00OyauuYRK0GlqWRU9KtP174ucJ_-kAAAF2fhYeLw; __csrf=0f7b86777ffd7330b698a30db2258be2; __remember_me=true; mode=TM1701; ntes_kaola_ad=1");
        String decryptParams=WyEncrypt.eapiDecrypt(params);
        String requestPath= apiServer[0]+decryptParams.substring(0,decryptParams.indexOf("-")).replace("api","eapi");
        String queryParams=decryptParams.substring(decryptParams.indexOf("{"),decryptParams.lastIndexOf("}")+1).replace("true","false");
        String header=queryParams.substring(queryParams.indexOf("\"header\":\"")+"\"header\":\"".length()+1);
        header=header.substring(0,header.indexOf("}"));
        queryParams=queryParams.replace(header,"");
        System.out.println(requestPath);
        System.out.println(queryParams);
        String param=WyEncrypt.eapiEncrypt(requestPath,queryParams);
        String response=HttpRequestHelper.downloadWebSiteUsePost(requestPath,"params="+param,headerMap);
        System.out.println(response);
    }

    @Test
    public void testEapi(){
        System.out.println(getTopList());
    }
    public static String getTopList(){
        String queryParams="{\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/weapi/toplist";
        String params=CryptoUtil.weapiEncrypt(queryParams);
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Content-Type","application/x-www-form-urlencoded");
        return HttpRequestHelper.downloadWebSiteUsePost(api,params,headerMap);
    }
}
