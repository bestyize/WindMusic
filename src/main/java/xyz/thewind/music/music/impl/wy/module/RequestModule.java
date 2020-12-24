package xyz.thewind.music.music.impl.wy.module;

import xyz.thewind.music.music.impl.wy.enc.CryptoUtil;
import xyz.thewind.music.music.impl.wy.enc.WyEncrypt;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.IpHelper;
import xyz.thewind.music.util.Md5Encrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static xyz.thewind.music.music.common.SongUtil.*;
import static xyz.thewind.music.music.common.SongUtil.QUALITY_LQ;

public class RequestModule {
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
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        mvQualityMap.put(QUALITY_SQ,"1080");
        mvQualityMap.put(QUALITY_HQ,"720");
        mvQualityMap.put(QUALITY_PQ,"480");
        mvQualityMap.put(QUALITY_LQ,"240");

    }
    public static String[] apiServer={"https://interface.music.163.com","https://music.163.com"};
    public static String searchSong(String keyword, int offset, int num){
        String queryParams="{\"s\":\""+keyword+"\",\"offset\":"+offset+",\"limit\":"+num+",\"header\":\"{}\",\"e_r\":\"false\",\"scene\":\"normal\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v1/search/song/get";
        String params= WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }
    //{"c":"[{\"id\":1497799734,\"v\":4},{\"id\":1497799734,\"v\":4}]","e_r":false,"header":"{}"}
    public static String getSongDetail(String songId){
        String queryParams="{\"c\":\"[{\\\"id\\\":"+songId+",\\\"v\\\":3}]\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v3/song/detail";
        String params= WyEncrypt.eapiEncrypt(api,queryParams);
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
        String queryParams="{\"password\":\""+ Md5Encrypt.convertToMd5(password)+"\",\"type\":\"1\",\"remember\":\"true\",\"https\":\"true\",\"checkToken\":\"9ca17ae2e6ffcda170e2e6eeb7e270acf1babaef609b9e8eb6d85f968f9f84f86a81afbca3f96d8f8bfba4c52af0feaec3b92ae9b8b894c145a289a6b8dc4e928f8fb2c54e9baeaba5b57e95ad828ded219486ee9e\",\"phone\":\""+phoneNumber+"\",\"countrycode\":\"86\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/w/login/cellphone";
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }

    public static String sendMessage(String phone){
        String queryParams="{\"cellphone\":\""+phone+"\",\"ctcode\":\"86\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/sms/captcha/sent";
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
//        也可用weapi的接口，eapi支持的它都支持
//        String api=apiServer[new Random().nextInt(apiServer.length)]+"/weapi/toplist";
//        String params= CryptoUtil.weapiEncrypt(queryParams);
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

    public static String getAlbumDetail(String albumId){
        String queryParams="{\"id\":\""+albumId+"\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/v1/album/"+albumId;
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }

    public static String getSimPlayList(String songId, int offset, int num){
        String queryParams="{\"songid\":\""+songId+"\",\"offset\":\""+offset+"\",\"total\":\"true\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/discovery/simiPlaylist";
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }
    public static String getSimSongList(String songId, int offset, int num){
        String queryParams="{\"songid\":\""+songId+"\",\"offset\":\""+offset+"\",\"total\":\"true\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/discovery/simiSong";
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }
    //{"offset":"0","total":"false","limit":"20","liveType":"1","channel":"2","e_r":false,"header":"{}"}

    /**
     *
     * @param offset
     * @param num
     * @param liveType look直播：看看：1 听听：2
     * @param channel
     * @return
     */
    public static String liveRecommend(int offset,int num,int liveType,int channel){
        String queryParams="{\"offset\":\""+offset+"\",\"total\":\"false\",\"limit\":\""+num+"\",\"liveType\":\""+liveType+"\",\"channel\":\""+channel+"\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/livestream/dragonball/recommend/v1";
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }

    /**
     * 节目排行榜
     * @param offset
     * @param num
     * @return
     */
    public static String getProgramTopList(int offset,int num){
        String queryParams="{\"offset\":\""+offset+"\",\"total\":\"false\",\"limit\":\""+num+"\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/program/toplist/v1";
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }

    /**
     * 电台排行榜
     * @param offset
     * @param num
     * @param type 新晋电台榜：0，热门电台榜：1
     * @return
     */
    public static String getDjRadioTopList(int offset,int num,int type){
        String queryParams="{\"offset\":\""+offset+"\",\"total\":\"false\",\"limit\":\""+num+"\",\"type\":\""+type+"\",\"e_r\":false,\"header\":\"{}\"}";
        String api=apiServer[new Random().nextInt(apiServer.length)]+"/eapi/djradio/toplist";
        String params=WyEncrypt.eapiEncrypt(api,queryParams);
        return HttpRequestHelper.downloadWebSiteUsePost(api,"params="+params,headerMap);
    }


}