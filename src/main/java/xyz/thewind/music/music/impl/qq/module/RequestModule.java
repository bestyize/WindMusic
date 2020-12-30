package xyz.thewind.music.music.impl.qq.module;

import xyz.thewind.music.music.impl.qq.bean.RequestTemplate;
import xyz.thewind.music.music.impl.qq.bean.RequestTemplateBuilder;
import xyz.thewind.music.music.impl.qq.enc.QqEncrypt;
import xyz.thewind.music.util.HttpRequestHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RequestModule {
    /**
     * musicu支持加密请求和不加密请求，没有对请求进行限制
     * musics仅支持加密请求
     */
    private static final String[] apiServer={"https://u.y.qq.com/cgi-bin/musicu.fcg?","https://u.y.qq.com/cgi-bin/musics.fcg?"};
    private static final Map<String,String> headerMap=new HashMap<>();

    static {
        headerMap.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66");
        headerMap.put("accept-encoding","application/json");
        headerMap.put("cache-control","max-age=0");
    }

    /**
     * 获取下载链接，这里暂时借用QMD的API实现
     * @param songId
     * @param quality
     * @return
     */
    public static String getPlayUrl(String songId,String quality){
        return ThirdPartApi.getPlayUrl(songId,quality);
    }


    /**
     * 请求示例：{"area":-100,"sex":-100,"genre":-100,"index":15,"sin":0,"cur_page":1}
     * @param area 地区 -100 不限
     * @param genre 流派 全部：-100，流行：1 ，摇滚：2，民谣：3，电子：4，爵士：5，嘻哈：6 .....
     * @param index 索引 热门:-100  范围 0-26 对应歌手名称拼音A-Z开头
     * @param sex 性别 不限：-100  男：0 女：1 组合：2
     * @param sin 偏移(第sin位)
     * @param currPage 当前页，第n页，默认每页最多80人
     * @return
     */
    public static String getSingerList(int area,int genre,int index,int sex,int sin,int currPage){
        RequestTemplate template=new RequestTemplate("Music.SingerListServer","get_singer_list","{\"area\":"+area+",\"sex\":"+sex+",\"genre\":"+genre+",\"index\":"+index+",\"sin\":"+sin+",\"cur_page\":"+currPage+"}");
        RequestTemplateBuilder templateBuilder=new RequestTemplateBuilder();
        templateBuilder.add("singerList",template);
        String sign= QqEncrypt.getSign(templateBuilder.toString());
        Random ran=new Random(System.currentTimeMillis());
        return HttpRequestHelper.downloadWebSiteUsePost(apiServer[ran.nextInt(apiServer.length)]+"sign="+sign,templateBuilder.toString(),headerMap);
    }

    public static String getHotSingerList(){
        return getSingerList(-100,-100,-100,-100,0,1);
    }

    /**
     *
     * @param songId 统一规范，指的是songmid
     * @return
     */
    public static String getSongDetail(String songId){
        //{"songinfo":{"module":"music.pf_song_detail_svr","method":"get_song_detail","param":{"song_mid":"112344"}}}
        RequestTemplate template=new RequestTemplate("music.pf_song_detail_svr","get_song_detail","{\"song_mid\":\""+songId+"\"}");
        RequestTemplateBuilder templateBuilder=new RequestTemplateBuilder();
        templateBuilder.add("songdetail",template);
        String sign= QqEncrypt.getSign(templateBuilder.toString());
        Random ran=new Random(System.currentTimeMillis());
        return HttpRequestHelper.downloadWebSiteUsePost(apiServer[ran.nextInt(apiServer.length)] +"sign="+sign,templateBuilder.toString(),headerMap);
    }

    public static String login(String accessToken,String refreshToken){
        long expireTime=System.currentTimeMillis()/1000+3600*24*365;
        RequestTemplate loginTemplate=new RequestTemplate("QQConnectLogin.LoginServer","QQLogin","{\"access_token\":\""+accessToken+"\",\"appid\":100497308,\"expired_in\":"+expireTime+",\"forceRefreshToken\":0,\"onlyNeedAccessToken\":0,\"openid\":\"54DF0A67E306595CEC62AF02E6176720\",\"refresh_token\":\""+refreshToken+"\"}");
        RequestTemplateBuilder builder=new RequestTemplateBuilder();
        builder.add("data",loginTemplate);
        return HttpRequestHelper.downloadWebSiteUsePost(apiServer[0],builder.toString(),null);
        //"comm":{"_channelid":"19","_os_version":"6.2.9200-2","ct":"19","cv":"1791","guid":"3954C437586B509983A6805AD81121C3","patch":"118","psrf_access_token_expiresAt":0,"psrf_qqaccess_token":"","psrf_qqopenid":"","psrf_qqunionid":"","tmeAppID":"qqmusic","tmeLoginType":2,"uin":"0","wid":"0"}
    }

}
